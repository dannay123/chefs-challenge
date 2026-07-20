package com.chefchallenge.client.ui;

import com.chefchallenge.client.model.DishDisplayDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * The main gameplay panel for one customer's dish.
 * Layout:
 *   NORTH  — score header + timer bar
 *   CENTER — scrollable content (dish card, hint panel, ingredient picker, technique builder)
 *   SOUTH  — submit button
 */
public class KitchenPanel extends JPanel {

    private static final int HINT_COST = 10;

    private static final String[] ALL_INGREDIENTS = {
        "Pizza Dough","Tomato Sauce","Mozzarella","Basil","Pasta",
        "Eggs","Pancetta","Parmesan","Romaine Lettuce","Croutons",
        "Beef Patty","Brioche Bun","Chicken","Bell Pepper","Chocolate"
    };
    private static final String[] ALL_TECHNIQUES = {
        "BAKE","BOIL","FRY","GRILL","CHOP","MIX","SEASON","FOLD","MARINATE","TOSS"
    };

    // State
    private final Set<String>  selectedIngredients = new LinkedHashSet<>();
    private final List<String> techniqueSequence   = new ArrayList<>();
    private int   timeRemaining;
    private final int totalTime;
    private javax.swing.Timer countdownTimer;

    // UI refs
    private JLabel  timerLabel;
    private JLabel  scoreLabel;
    private JLabel  hintsLabel;
    private JLabel  statusLabel;
    private JButton submitBtn;
    private JPanel  sequenceZone;
    private JLayeredPane layeredPane;
    private JPanel  hintRevealPanel;
    private JButton hintIngBtn, hintTechBtn;
    private JLabel  hintStatusLabel;

    private final MainFrame frame;
    private int currentScore;
    private int hintsLeft;

    public KitchenPanel(DishDisplayDTO dish, int customerNum, int totalCustomers,
                        int score, int hints, int timeLimit, MainFrame frame) {
        this.frame         = frame;
        this.currentScore  = score;
        this.hintsLeft     = hints;
        this.totalTime     = timeLimit;
        this.timeRemaining = timeLimit;

        setLayout(new BorderLayout(0, 0));
        setBackground(MainFrame.BG);

        add(buildHeader(customerNum, totalCustomers, score), BorderLayout.NORTH);
        add(buildCenter(dish), BorderLayout.CENTER);
        add(buildSouth(), BorderLayout.SOUTH);

        refreshHintButtons();
        startTimer();
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel buildHeader(int num, int total, int score) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(MainFrame.ACCENT);
        p.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(MainFrame.ACCENT);
        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);
        JLabel cust = new JLabel("Customer " + num + " / " + total);
        cust.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cust.setForeground(Color.WHITE);
        left.add(scoreLabel);
        left.add(new JLabel("  |  ") {{ setForeground(Color.WHITE); }});
        left.add(cust);

        timerLabel = new JLabel(toMMSS(timeRemaining));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        timerLabel.setForeground(Color.WHITE);

        hintsLabel = new JLabel("Hints: " + hintsLeft);
        hintsLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hintsLabel.setForeground(new Color(0xFFE0B2));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(MainFrame.ACCENT);
        right.add(hintsLabel);
        right.add(timerLabel);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Center ────────────────────────────────────────────────────────────────

    private JScrollPane buildCenter(DishDisplayDTO dish) {
        // ScrollablePanel forces content width == viewport width so JTextArea
        // and FlowLayout always see the correct width during preferred-size queries.
        // This eliminates the horizontal scrollbar and all width-driven clipping.
        ScrollablePanel content = new ScrollablePanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MainFrame.BG);
        content.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        content.add(buildDishCard(dish));
        content.add(Box.createVerticalStrut(12));
        content.add(buildHintPanel());
        content.add(Box.createVerticalStrut(12));
        content.add(sectionLabel("🥦 Pick Your Ingredients:"));
        content.add(Box.createVerticalStrut(6));
        content.add(buildIngredientPicker());
        content.add(Box.createVerticalStrut(12));
        content.add(sectionLabel("🔪 Arrange Your Techniques:"));
        content.add(Box.createVerticalStrut(6));
        content.add(buildTechniqueArea());

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(40);
        scroll.getVerticalScrollBar().setBlockIncrement(60);
        return scroll;
    }

    private JPanel buildDishCard(DishDisplayDTO dish) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MainFrame.PANEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.GOLD, 2),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel orderLabel = new JLabel("Customer order");
        orderLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        orderLabel.setForeground(MainFrame.ACCENT);
        orderLabel.setAlignmentX(LEFT_ALIGNMENT);
        card.add(orderLabel);
        card.add(Box.createVerticalStrut(6));

        JTextArea desc = new JTextArea(dish.getDescription());
        desc.setFont(new Font("SansSerif", Font.ITALIC, 13));
        desc.setForeground(MainFrame.TEXT);
        desc.setBackground(MainFrame.PANEL_BG);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setAlignmentX(LEFT_ALIGNMENT);
        card.add(desc);
        card.add(Box.createVerticalStrut(6));

        if (dish.getVisualDetails() != null && !dish.getVisualDetails().isEmpty()) {
            JLabel vdLabel = new JLabel(dish.getVisualDetails());
            vdLabel.setFont(MainFrame.SMALL_FONT);
            vdLabel.setForeground(new Color(0x6B4226));
            vdLabel.setAlignmentX(LEFT_ALIGNMENT);
            card.add(vdLabel);
        }
        return card;
    }

    private JPanel buildHintPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 4));
        p.setBackground(MainFrame.PANEL_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xCCBBA0)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setBackground(MainFrame.PANEL_BG);
        JLabel hintTitle = new JLabel("💡 Hints  (each costs 10 pts)");
        hintTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        hintTitle.setForeground(MainFrame.TEXT);

        hintIngBtn  = smallButton("Ingredient Hint");
        hintTechBtn = smallButton("Technique Hint");
        hintIngBtn.addActionListener(e  -> useHint("INGREDIENT"));
        hintTechBtn.addActionListener(e -> useHint("TECHNIQUE"));

        hintStatusLabel = new JLabel("");
        hintStatusLabel.setFont(MainFrame.SMALL_FONT);
        hintStatusLabel.setForeground(new Color(0xC0392B));

        btnRow.add(hintTitle);
        btnRow.add(hintIngBtn);
        btnRow.add(hintTechBtn);
        btnRow.add(hintStatusLabel);
        p.add(btnRow, BorderLayout.NORTH);

        hintRevealPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        hintRevealPanel.setBackground(MainFrame.PANEL_BG);
        p.add(hintRevealPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildIngredientPicker() {
        // FlowLayout.preferredLayoutSize() never wraps (puts all chips in one row → height=46px).
        // BoxLayout Y uses preferred height, so we must override it to report the actual wrapped height.
        // Chips are 118×34 with hgap=8, vgap=6. At min window width 700px → 5 per row → 3 rows → 126px.
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6)) {
            @Override public Dimension getPreferredSize() { return new Dimension(1, 130); }
            @Override public Dimension getMaximumSize()   { return new Dimension(Integer.MAX_VALUE, 130); }
        };
        grid.setBackground(MainFrame.BG);

        for (String ing : ALL_INGREDIENTS) {
            IngredientToggle toggle = new IngredientToggle(ing, name -> {
                if (selectedIngredients.contains(name)) selectedIngredients.remove(name);
                else selectedIngredients.add(name);
                refreshSubmit();
            });
            toggle.setToolTipText(ing);  // used by paintComponent for label text
            grid.add(toggle);
        }
        return grid;
    }

    private JPanel buildTechniqueArea() {
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(780, 180));
        layeredPane.setBackground(MainFrame.BG);
        layeredPane.setOpaque(true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(MainFrame.BG);
        content.setBounds(0, 0, 780, 180);
        layeredPane.add(content, JLayeredPane.DEFAULT_LAYER);

        // Resize content to fill the pane whenever the window is resized.
        // setBounds() calls invalidate() internally, scheduling a correct second-pass
        // layout without triggering a revalidate loop.
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();
                if (w > 0 && h > 0) content.setBounds(0, 0, w, h);
            }
        });

        // Technique pool
        JPanel pool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pool.setBackground(MainFrame.BG);
        for (String tech : ALL_TECHNIQUES) {
            TechChip chip = new TechChip(tech);
            chip.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e)  { startDrag(chip, e); }
                public void mouseReleased(MouseEvent e) { endDrag(chip, e); }
            });
            chip.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e)  { updateDrag(chip, e); }
            });
            pool.add(chip);
        }
        content.add(pool);
        content.add(Box.createVerticalStrut(8));

        // Sequence zone
        sequenceZone = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        sequenceZone.setBackground(new Color(0xFDE8CC));
        sequenceZone.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MainFrame.ACCENT, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        sequenceZone.setMinimumSize(new Dimension(0, 54));
        sequenceZone.setPreferredSize(new Dimension(200, 54));
        refreshSequenceZone();
        content.add(sequenceZone);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(MainFrame.BG);
        wrapper.add(layeredPane);
        return wrapper;
    }

    // ── South ─────────────────────────────────────────────────────────────────

    private JPanel buildSouth() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        p.setBackground(MainFrame.BG);
        statusLabel = new JLabel("Pick at least 1 ingredient and 1 technique.");
        statusLabel.setFont(MainFrame.SMALL_FONT);
        statusLabel.setForeground(new Color(0x8B5E3C));
        submitBtn = StartPanel.styledButton("Submit Dish");
        submitBtn.setEnabled(false);
        submitBtn.addActionListener(e -> onSubmit());
        p.add(statusLabel);
        p.add(submitBtn);
        return p;
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private void startTimer() {
        countdownTimer = new javax.swing.Timer(1000, e -> {
            timeRemaining--;
            updateTimerDisplay();
            if (timeRemaining <= 0) {
                countdownTimer.stop();
                onTimeout();
            }
        });
        countdownTimer.start();
    }

    private String toMMSS(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private void updateTimerDisplay() {
        timerLabel.setText(toMMSS(timeRemaining));
        double ratio = (double) timeRemaining / totalTime;
        timerLabel.setForeground(ratio > 0.5
                ? new Color(0xA5D6A7)
                : ratio > 0.25 ? new Color(0xFFCC80)
                : new Color(0xEF9A9A));
    }

    // ── Hints ─────────────────────────────────────────────────────────────────

    private void refreshHintButtons() {
        boolean enabled = hintsLeft > 0 && currentScore >= HINT_COST;
        hintIngBtn.setEnabled(enabled);
        hintTechBtn.setEnabled(enabled);
    }

    private void useHint(String hintType) {
        if (currentScore < HINT_COST) {
            hintStatusLabel.setText("Insufficient points for a hint.");
            return;
        }
        hintIngBtn.setEnabled(false);
        hintTechBtn.setEnabled(false);
        hintStatusLabel.setText("");
        frame.requestHint(hintType, hint -> {
            hintStatusLabel.setText("");
            JLabel chip = new JLabel(" " + ("INGREDIENT".equals(hint.getHintType()) ? "🥦 " : "🔪 ")
                                    + hint.getRevealed() + " ");
            chip.setFont(MainFrame.SMALL_FONT);
            chip.setForeground(MainFrame.TEXT);
            chip.setOpaque(true);
            chip.setBackground(new Color(0xE8F5E9));
            chip.setBorder(BorderFactory.createLineBorder(new Color(0x81C784)));
            hintRevealPanel.add(chip);
            hintRevealPanel.revalidate();
            hintRevealPanel.repaint();

            hintsLeft = hint.getHintsRemaining();
            currentScore = Math.max(0, hint.getNewScore());
            hintsLabel.setText("Hints: " + hintsLeft);
            scoreLabel.setText("Score: " + currentScore);

            refreshHintButtons();
        }, msg -> {
            hintStatusLabel.setText("Insufficient points for a hint.");
            refreshHintButtons();
        });
    }

    // ── Submit / Timeout ──────────────────────────────────────────────────────

    private void onSubmit() {
        countdownTimer.stop();
        submitBtn.setEnabled(false);
        frame.submitDish(new ArrayList<>(selectedIngredients), new ArrayList<>(techniqueSequence));
    }

    private void onTimeout() {
        submitBtn.setEnabled(false);
        frame.sendTimeout();
    }

    // ── Drag-and-drop ─────────────────────────────────────────────────────────

    private JLabel dragImage;

    private void startDrag(TechChip chip, MouseEvent e) {
        BufferedImage img = new BufferedImage(chip.getWidth(), chip.getHeight(), BufferedImage.TYPE_INT_ARGB);
        chip.paint(img.getGraphics());
        dragImage = new JLabel(new ImageIcon(img));
        dragImage.setSize(chip.getWidth(), chip.getHeight());
        Point p = SwingUtilities.convertPoint(chip, e.getPoint(), layeredPane);
        dragImage.setLocation(p.x - chip.getWidth()/2, p.y - chip.getHeight()/2);
        layeredPane.add(dragImage, JLayeredPane.DRAG_LAYER);
        layeredPane.repaint();
    }

    private void updateDrag(TechChip chip, MouseEvent e) {
        if (dragImage == null) return;
        Point p = SwingUtilities.convertPoint(chip, e.getPoint(), layeredPane);
        dragImage.setLocation(p.x - dragImage.getWidth()/2, p.y - dragImage.getHeight()/2);
        layeredPane.repaint();
    }

    private void endDrag(TechChip chip, MouseEvent e) {
        if (dragImage == null) return;
        layeredPane.remove(dragImage);
        dragImage = null;
        layeredPane.repaint();

        Point p = SwingUtilities.convertPoint(chip, e.getPoint(), layeredPane);
        Point zLoc = SwingUtilities.convertPoint(sequenceZone.getParent(),
                sequenceZone.getLocation(), layeredPane);
        Rectangle zBounds = new Rectangle(zLoc.x, zLoc.y,
                sequenceZone.getWidth(), sequenceZone.getHeight());

        if (zBounds.contains(p) && !techniqueSequence.contains(chip.tech)) {
            techniqueSequence.add(chip.tech);
            refreshSequenceZone();
            refreshSubmit();
        }
    }

    private void refreshSequenceZone() {
        sequenceZone.removeAll();
        if (techniqueSequence.isEmpty()) {
            JLabel ph = new JLabel("← Drag techniques here in order →");
            ph.setFont(MainFrame.SMALL_FONT);
            ph.setForeground(new Color(0xAA8866));
            sequenceZone.add(ph);
        } else {
            for (int i = 0; i < techniqueSequence.size(); i++) {
                final String t = techniqueSequence.get(i);
                JLabel num  = new JLabel((i+1) + ".");
                num.setFont(new Font("SansSerif", Font.BOLD, 12));
                num.setForeground(MainFrame.ACCENT);
                JLabel name = new JLabel(capitalize(t));
                name.setFont(new Font("SansSerif", Font.BOLD, 12));
                JButton rm = new JButton("✕");
                rm.setFont(new Font("SansSerif", Font.BOLD, 10));
                rm.setForeground(MainFrame.ACCENT);
                rm.setBackground(new Color(0xFDE8CC));
                rm.setBorderPainted(false); rm.setContentAreaFilled(false); rm.setFocusPainted(false);
                rm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                rm.addActionListener(ev -> { techniqueSequence.remove(t); refreshSequenceZone(); refreshSubmit(); });
                JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
                entry.setBackground(new Color(0xFDE8CC));
                entry.add(num); entry.add(name); entry.add(rm);
                sequenceZone.add(entry);
                if (i < techniqueSequence.size()-1) {
                    JLabel arr = new JLabel("→");
                    arr.setFont(new Font("SansSerif", Font.BOLD, 14));
                    arr.setForeground(MainFrame.ACCENT);
                    sequenceZone.add(arr);
                }
            }
        }
        sequenceZone.revalidate();
        sequenceZone.repaint();
    }

    private void refreshSubmit() {
        boolean ready = !selectedIngredients.isEmpty() && !techniqueSequence.isEmpty();
        submitBtn.setEnabled(ready);
        if (ready) statusLabel.setText("Ready! Click Submit Dish.");
        else statusLabel.setText("Pick at least 1 ingredient and 1 technique.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(MainFrame.TEXT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JButton smallButton(String text) {
        JButton b = new JButton(text);
        b.setFont(MainFrame.SMALL_FONT);
        b.setBackground(MainFrame.GOLD);
        b.setForeground(MainFrame.TEXT);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private String capitalize(String s) {
        return s.isEmpty() ? s : s.charAt(0) + s.substring(1).toLowerCase();
    }

    // ── Inner: IngredientToggle ───────────────────────────────────────────────

    static class IngredientToggle extends JPanel {
        private boolean on = false;
        IngredientToggle(String name, java.util.function.Consumer<String> onToggle) {
            setPreferredSize(new Dimension(118, 34));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { on = !on; repaint(); onToggle.accept(name); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(on ? MainFrame.GOLD : new Color(0xE0D5C8));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(on ? MainFrame.TEXT : new Color(0x555555));
            g2.setFont(new Font("SansSerif", on ? Font.BOLD : Font.PLAIN, 11));
            String label = (on ? "✓ " : "") + ((JPanel)this).getToolTipText();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (getWidth()-fm.stringWidth(label))/2, getHeight()/2+4);
            g2.dispose();
        }
    }

    // ── Inner: ScrollablePanel ────────────────────────────────────────────────
    // Implementing Scrollable with getScrollableTracksViewportWidth()=true forces
    // the JScrollPane viewport to set this panel's width = viewport width before
    // any preferred-size queries run.  That means JTextArea, FlowLayout, and
    // BoxLayout all see the correct width — so no inflated preferred width, no
    // horizontal scrollbar, and FlowLayout wraps chips at the actual column count.

    private static class ScrollablePanel extends JPanel implements javax.swing.Scrollable {
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        public int  getScrollableUnitIncrement(Rectangle r, int axis, int dir) { return 40; }
        public int  getScrollableBlockIncrement(Rectangle r, int axis, int dir) { return 60; }
        public boolean getScrollableTracksViewportWidth()  { return true;  }
        public boolean getScrollableTracksViewportHeight() { return false; }
    }

    // ── Inner: TechChip ───────────────────────────────────────────────────────

    static class TechChip extends JPanel {
        final String tech;
        TechChip(String tech) {
            this.tech = tech;
            setPreferredSize(new Dimension(88, 34));
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0x80CBC4));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.setColor(MainFrame.TEXT);
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            String label = tech.charAt(0) + tech.substring(1).toLowerCase();
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, (getWidth()-fm.stringWidth(label))/2, getHeight()/2+4);
            g2.dispose();
        }
    }
}
