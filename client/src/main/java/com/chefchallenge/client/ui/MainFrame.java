package com.chefchallenge.client.ui;

import com.chefchallenge.client.api.ApiClient;
import com.chefchallenge.client.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainFrame extends JFrame {

    static final Color BG         = new Color(0xFFF8F0);
    static final Color ACCENT     = new Color(0xE05C2D);
    static final Color GOLD       = new Color(0xF0A500);
    static final Color TEXT       = new Color(0x2C1A0E);
    static final Color PANEL_BG   = new Color(0xFDEFE0);
    static final Font  TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    static final Font  BODY_FONT  = new Font("SansSerif", Font.PLAIN, 14);
    static final Font  SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    private static final int TOTAL_CUSTOMERS  = 6;
    private static final int HINTS_PER_DISH   = 3;

    private final ApiClient  api    = new ApiClient();
    private final CardLayout layout = new CardLayout();
    private final JPanel     cards  = new JPanel(layout);

    private String sessionId;
    private int    currentCustomer;
    private int    currentScore;

    public MainFrame() {
        super("Chef's Challenge");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);

        cards.setBackground(BG);
        cards.add(new StartPanel(this::onStart), "START");
        add(cards);
        layout.show(cards, "START");
        setVisible(true);
    }

    void showCard(String name) { layout.show(cards, name); }

    private void onStart() {
        new SwingWorker<StartSessionResponse, Void>() {
            @Override protected StartSessionResponse doInBackground() throws Exception {
                return api.startSession();
            }
            @Override protected void done() {
                try {
                    StartSessionResponse r = get();
                    sessionId       = r.getSessionId();
                    currentCustomer = 1;
                    currentScore    = 0;
                    showKitchen(r.getDishDisplay(), r.getTimeLimit());
                } catch (Exception ex) {
                    showError("Cannot connect to server on port 8080.\n" + ex.getMessage());
                }
            }
        }.execute();
    }

    void submitDish(List<String> ingredients, List<String> techniques) {
        new SwingWorker<SubmitResponse, Void>() {
            @Override protected SubmitResponse doInBackground() throws Exception {
                return api.submitDish(sessionId, ingredients, techniques);
            }
            @Override protected void done() {
                try {
                    SubmitResponse r = get();
                    currentScore = r.getGrade().getTotalScore();
                    currentCustomer++;
                    showResult(r);
                } catch (Exception ex) { showError("Submit failed: " + ex.getMessage()); }
            }
        }.execute();
    }

    void sendTimeout() {
        new SwingWorker<SubmitResponse, Void>() {
            @Override protected SubmitResponse doInBackground() throws Exception {
                return api.sendTimeout(sessionId);
            }
            @Override protected void done() {
                try {
                    SubmitResponse r = get();
                    currentScore = r.getGrade().getTotalScore();
                    currentCustomer++;
                    showResult(r);
                } catch (Exception ex) { showError("Timeout call failed: " + ex.getMessage()); }
            }
        }.execute();
    }

    void requestHint(String hintType,
                     java.util.function.Consumer<HintResponse> onSuccess,
                     java.util.function.Consumer<String> onError) {
        new SwingWorker<HintResponse, Void>() {
            @Override protected HintResponse doInBackground() throws Exception {
                return api.requestHint(sessionId, hintType);
            }
            @Override protected void done() {
                try { onSuccess.accept(get()); }
                catch (Exception ex) { onError.accept(ex.getMessage()); }
            }
        }.execute();
    }

    private void showResult(SubmitResponse r) {
        cards.add(new ResultPanel(r, this::onNextCustomer, this::onStart), "RESULT");
        layout.show(cards, "RESULT");
    }

    private void onNextCustomer(SubmitResponse r) {
        showKitchen(r.getNextDish(), r.getTimeLimit());
    }

    private void showKitchen(DishDisplayDTO dish, int timeLimit) {
        KitchenPanel kitchen = new KitchenPanel(
            dish, currentCustomer, TOTAL_CUSTOMERS, currentScore, HINTS_PER_DISH, timeLimit, this);
        cards.add(kitchen, "KITCHEN");
        layout.show(cards, "KITCHEN");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
