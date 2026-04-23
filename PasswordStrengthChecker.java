import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class PasswordStrengthChecker extends JFrame {
    private JPasswordField passwordField;
    private JLabel strengthLabel, suggestionsLabel;
    private JProgressBar progressBar;

    private final Color weakColor = new Color(255, 75, 75), mediumColor = new Color(255, 170, 0), strongColor = new Color(0, 200, 83);
    private final Color textColor = new Color(50, 50, 50), accentColor = new Color(108, 92, 231);

    public PasswordStrengthChecker() {
        setTitle("Password Strength Checker");
        setSize(650, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel wrapperPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new LinearGradientPaint(0, 0, getWidth(), getHeight(), new float[]{0f, 0.5f, 1f}, 
                    new Color[]{new Color(65, 88, 208), new Color(200, 80, 192), new Color(255, 204, 112)}));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        passwordField.setBackground(new Color(240, 245, 250));
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(accentColor);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235), 2, true), new EmptyBorder(12, 15, 12, 15)));
        passwordField.setPreferredSize(new Dimension(380, 55));
        passwordField.setMaximumSize(new Dimension(380, 55));
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { evaluate(); }
            public void removeUpdate(DocumentEvent e) { evaluate(); }
            public void changedUpdate(DocumentEvent e) { evaluate(); }
        });

        JCheckBox toggle = new JCheckBox("Show Password");
        toggle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toggle.setForeground(new Color(110, 110, 110));
        toggle.setFocusPainted(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggle.setOpaque(false);
        toggle.addActionListener(e -> passwordField.setEchoChar(toggle.isSelected() ? (char) 0 : '\u2022'));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        progressBar.setBackground(new Color(235, 235, 235));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(380, 30));
        progressBar.setMaximumSize(new Dimension(380, 30));
        progressBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected Color getSelectionBackground() { return textColor; }
            protected Color getSelectionForeground() { return Color.WHITE; }
        });

        strengthLabel = createLabel("Strength: 0%", Font.BOLD, 18, new Color(100, 100, 100));
        suggestionsLabel = createLabel(" ", Font.ITALIC, 14, new Color(130, 130, 130));

        addComp(mainPanel, createLabel("Password Strength", Font.BOLD, 30, accentColor), 8);
        addComp(mainPanel, createLabel("Type a password to check its security", Font.PLAIN, 15, new Color(130, 130, 130)), 30);
        addComp(mainPanel, passwordField, 12);
        addComp(mainPanel, toggle, 30);
        addComp(mainPanel, progressBar, 15);
        addComp(mainPanel, strengthLabel, 15);
        addComp(mainPanel, suggestionsLabel, 0);

        wrapperPanel.add(mainPanel);
        add(wrapperPanel);
    }

    private JLabel createLabel(String txt, int style, int size, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(c);
        return l;
    }

    private void addComp(JPanel p, JComponent c, int space) {
        c.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(c);
        if (space > 0) p.add(Box.createRigidArea(new Dimension(0, space)));
    }

    private void evaluate() {
        String pwd = new String(passwordField.getPassword());
        if (pwd.isEmpty()) { updateUI(0, "Weak", weakColor, " "); return; }

        int score = pwd.length() < 6 ? 10 : (pwd.length() <= 10 ? 25 : 50);
        int flags = 0;
        for (char c : pwd.toCharArray()) 
            flags |= Character.isLowerCase(c) ? 1 : (Character.isUpperCase(c) ? 2 : (Character.isDigit(c) ? 4 : 8));

        StringBuilder sugg = new StringBuilder("<html><div style='text-align: center;'>");
        if ((flags & 1) > 0) score += 10; else sugg.append("Add lowercase letters.<br>");
        if ((flags & 2) > 0) score += 10; else sugg.append("Add uppercase letters.<br>");
        if ((flags & 4) > 0) score += 15; else sugg.append("Add numbers.<br>");
        if ((flags & 8) > 0) score += 15; else sugg.append("Add special characters.<br>");

        score = Math.min(score, 100);
        sugg.append((score > 70) ? "Great password!</div></html>" : "</div></html>");

        String txt = score <= 40 ? "Weak" : (score <= 70 ? "Medium" : "Strong");
        Color c = score <= 40 ? weakColor : (score <= 70 ? mediumColor : strongColor);
        updateUI(score, txt, c, sugg.toString());
    }

    private void updateUI(int score, String txt, Color c, String sugg) {
        progressBar.setValue(score);
        progressBar.setForeground(c);
        strengthLabel.setText("Strength: " + score + "% (" + txt + ")");
        strengthLabel.setForeground(c);
        suggestionsLabel.setText(sugg);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new PasswordStrengthChecker().setVisible(true));
    }
}
