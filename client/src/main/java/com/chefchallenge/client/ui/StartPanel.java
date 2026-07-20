package com.chefchallenge.client.ui;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {

    public StartPanel(Runnable onStart) {
        setLayout(new GridBagLayout());
        setBackground(MainFrame.BG);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.insets = new Insets(12, 10, 12, 10);

        JLabel title = new JLabel("🍳  Chef's Challenge");
        title.setFont(new Font("SansSerif", Font.BOLD, 38));
        title.setForeground(MainFrame.ACCENT);
        c.gridy = 0; add(title, c);

        JLabel sub = new JLabel("Recreate the dish. Satisfy your customers.");
        sub.setFont(MainFrame.BODY_FONT);
        sub.setForeground(MainFrame.TEXT);
        c.gridy = 1; add(sub, c);

        JTextArea rules = new JTextArea(
            "A customer arrives and shows you a picture of the dish they want.\n" +
            "You don't know the recipe — pick ingredients and techniques to recreate it.\n" +
            "Use hints for clues, but each hint costs 10 points.\n" +
            "Beat the clock. 6 customers. Highest score wins."
        );
        rules.setFont(MainFrame.SMALL_FONT);
        rules.setForeground(new Color(0x6B4226));
        rules.setBackground(MainFrame.BG);
        rules.setEditable(false);
        rules.setFocusable(false);
        rules.setAlignmentX(CENTER_ALIGNMENT);
        c.gridy = 2; add(rules, c);

        JButton btn = styledButton("Start Service");
        btn.addActionListener(e -> onStart.run());
        c.gridy = 3; add(btn, c);
    }

    static JButton styledButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? MainFrame.ACCENT.darker() : MainFrame.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText()))/2,
                              (getHeight() + fm.getAscent() - fm.getDescent())/2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
