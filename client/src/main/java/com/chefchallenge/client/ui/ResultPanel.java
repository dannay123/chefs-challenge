package com.chefchallenge.client.ui;

import com.chefchallenge.client.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ResultPanel extends JPanel {

    public ResultPanel(SubmitResponse r,
                       Consumer<SubmitResponse> onNext, Runnable onPlayAgain) {
        setLayout(new BorderLayout(0, 8));
        setBackground(MainFrame.BG);

        add(buildTierBanner(r.getGrade()),        BorderLayout.NORTH);
        add(buildBody(r),                         BorderLayout.CENTER);
        add(buildFooter(r, onNext, onPlayAgain),  BorderLayout.SOUTH);
    }

    // ── Tier banner ───────────────────────────────────────────────────────────

    private JPanel buildTierBanner(GradeResult g) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        Color bg;
        String icon;
        switch (g.getTier()) {
            case "PERFECT":  bg = new Color(0x1B5E20); icon = "🏆 PERFECT!"; break;
            case "GOOD":     bg = new Color(0x2E7D32); icon = "😊 GOOD";     break;
            case "BAD":      bg = new Color(0xE65100); icon = "😕 BAD";       break;
            case "TERRIBLE": bg = new Color(0xB71C1C); icon = "😡 TERRIBLE"; break;
            default:         bg = new Color(0x37474F); icon = "⏰ TIMEOUT";  break;
        }
        p.setBackground(bg);

        JLabel lbl = new JLabel(icon);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);

        JLabel pts = new JLabel((g.getPointsEarned() >= 0 ? "  +" : "  ") + g.getPointsEarned() + " pts");
        pts.setFont(new Font("SansSerif", Font.BOLD, 18));
        pts.setForeground(Color.WHITE);

        p.add(lbl);
        p.add(pts);
        return p;
    }

    // ── Body ──────────────────────────────────────────────────────────────────

    private JPanel buildBody(SubmitResponse r) {
        GradeResult g = r.getGrade();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(MainFrame.BG);
        p.setBorder(BorderFactory.createEmptyBorder(16, 40, 8, 40));

        // Similarity breakdown
        p.add(buildScoreRow("Ingredient score:", g.getIngredientScore()));
        p.add(Box.createVerticalStrut(6));
        p.add(buildScoreRow("Technique score:", g.getTechniqueScore()));
        p.add(Box.createVerticalStrut(6));
        int overallPct = (int)(g.getSimilarityScore() * 100);
        JLabel overall = new JLabel("Overall similarity: " + overallPct + "%");
        overall.setFont(new Font("SansSerif", Font.BOLD, 14));
        overall.setForeground(MainFrame.ACCENT);
        overall.setAlignmentX(LEFT_ALIGNMENT);
        p.add(overall);
        p.add(Box.createVerticalStrut(20));

        // Recipe reveal
        RecipeReveal reveal = r.getRecipeReveal();
        if (reveal != null) {
            JPanel revealCard = new JPanel();
            revealCard.setLayout(new BoxLayout(revealCard, BoxLayout.Y_AXIS));
            revealCard.setBackground(new Color(0xE8F5E9));
            revealCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x81C784), 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            revealCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

            JLabel dishName = new JLabel("The dish was: " + reveal.getDishName());
            dishName.setFont(new Font("SansSerif", Font.BOLD, 15));
            dishName.setForeground(new Color(0x1B5E20));
            dishName.setAlignmentX(LEFT_ALIGNMENT);

            JLabel ing = new JLabel("Ingredients: " + String.join(", ", reveal.getIngredients()));
            ing.setFont(MainFrame.SMALL_FONT);
            ing.setForeground(MainFrame.TEXT);
            ing.setAlignmentX(LEFT_ALIGNMENT);

            JLabel tech = new JLabel("Techniques: " + String.join(", ", reveal.getTechniques()));
            tech.setFont(MainFrame.SMALL_FONT);
            tech.setForeground(MainFrame.TEXT);
            tech.setAlignmentX(LEFT_ALIGNMENT);

            revealCard.add(dishName);
            revealCard.add(Box.createVerticalStrut(4));
            revealCard.add(ing);
            revealCard.add(Box.createVerticalStrut(2));
            revealCard.add(tech);
            p.add(revealCard);
        }
        return p;
    }

    private JPanel buildScoreRow(String label, double score) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(MainFrame.BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lbl = new JLabel(label);
        lbl.setFont(MainFrame.BODY_FONT);
        lbl.setPreferredSize(new Dimension(200, 20));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int)(score * 100));
        bar.setPreferredSize(new Dimension(200, 18));
        bar.setForeground(score >= 0.9 ? new Color(0x2E7D32)
                        : score >= 0.6 ? new Color(0xF57C00)
                        :                new Color(0xC62828));
        bar.setString((int)(score * 100) + "%");
        bar.setStringPainted(true);

        row.add(lbl);
        row.add(bar);
        row.setAlignmentX(LEFT_ALIGNMENT);
        return row;
    }

    // ── Footer ────────────────────────────────────────────────────────────────

    private JPanel buildFooter(SubmitResponse r,
                               Consumer<SubmitResponse> onNext, Runnable onPlayAgain) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        p.setBackground(MainFrame.BG);

        int totalScore = r.getGrade().getTotalScore();
        JLabel scoreLbl = new JLabel("Total score: " + totalScore);
        scoreLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        scoreLbl.setForeground(MainFrame.ACCENT);
        p.add(scoreLbl);

        if (r.isSessionOver()) {
            JLabel sessionOver = new JLabel("  — Session Over");
            sessionOver.setFont(new Font("SansSerif", Font.BOLD, 15));
            sessionOver.setForeground(new Color(0x1B5E20));
            p.add(sessionOver);
            JButton again = StartPanel.styledButton("Play Again");
            again.addActionListener(e -> onPlayAgain.run());
            p.add(again);
        } else {
            JButton next = StartPanel.styledButton("Next Customer →");
            next.addActionListener(e -> onNext.accept(r));
            p.add(next);
        }
        return p;
    }
}
