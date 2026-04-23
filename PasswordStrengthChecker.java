import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PasswordStrengthChecker {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("Password Strength Checker");
        frame.setSize(900, 700);
        frame.setMinimumSize(new Dimension(620, 560));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main Panel with custom gradient background
        JPanel mainPanel = new GradientPanel(new Color(16, 22, 40), new Color(34, 24, 56));
        mainPanel.setLayout(new GridBagLayout());

        // Center card panel
        JPanel formPanel = new RoundedCardPanel(30, new Color(250, 251, 253));
        formPanel.setLayout(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(620, 510));
        formPanel.setBorder(new EmptyBorder(40, 54, 40, 54));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Password Strength", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        titleLabel.setForeground(new Color(95, 89, 212));
        gbc.insets = new Insets(0, 0, 8, 0);
        formPanel.add(titleLabel, gbc);

        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Type a password to check its security", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        subtitleLabel.setForeground(new Color(136, 140, 149));
        gbc.insets = new Insets(0, 0, 34, 0);
        formPanel.add(subtitleLabel, gbc);

        // Password Field
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 18, 0);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        passwordField.setPreferredSize(new Dimension(300, 66));
        passwordField.setBackground(new Color(232, 235, 240));
        passwordField.setForeground(new Color(60, 62, 67));
        passwordField.setCaretColor(new Color(92, 87, 208));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 208, 216), 2),
                new EmptyBorder(8, 18, 8, 18)
        ));
        formPanel.add(passwordField, gbc);
        char defaultEchoChar = passwordField.getEchoChar();

        // Show Password Checkbox
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 32, 0);
        JCheckBox showPwd = new JCheckBox("Show Password");
        showPwd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        showPwd.setForeground(new Color(102, 106, 114));
        showPwd.setBackground(formPanel.getBackground());
        showPwd.setFocusPainted(false);
        showPwd.setHorizontalAlignment(SwingConstants.CENTER);
        showPwd.addActionListener(e -> passwordField.setEchoChar(showPwd.isSelected() ? (char) 0 : defaultEchoChar));
        JPanel checkWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        checkWrap.setOpaque(false);
        checkWrap.add(showPwd);
        formPanel.add(checkWrap, gbc);

        // Progress Bar
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 18, 0);
        JProgressBar strengthBar = new JProgressBar(0, 100);
        strengthBar.setPreferredSize(new Dimension(300, 38));
        strengthBar.setBackground(new Color(223, 225, 229));
        strengthBar.setForeground(new Color(95, 89, 212));
        strengthBar.setStringPainted(true);
        strengthBar.setString("0%");
        strengthBar.setBorder(BorderFactory.createEmptyBorder());
        strengthBar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        strengthBar.setUI(new javax.swing.plaf.basic.BasicProgressBarUI() {
            protected Color getSelectionBackground() { return new Color(70, 72, 80); }
            protected Color getSelectionForeground() { return new Color(70, 72, 80); }
        });
        formPanel.add(strengthBar, gbc);

        // Result Label
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel resultLabel = new JLabel("Strength: 0%", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        resultLabel.setForeground(new Color(90, 93, 102));
        formPanel.add(resultLabel, gbc);

        // Real-time strength checking logic
        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { check(); }
            public void removeUpdate(DocumentEvent e) { check(); }
            public void changedUpdate(DocumentEvent e) { check(); }
            private void check() {
                String p = new String(passwordField.getPassword());
                if (p.isEmpty()) {
                    strengthBar.setValue(0);
                    strengthBar.setString("0%");
                    resultLabel.setText("Strength: 0%");
                    resultLabel.setForeground(new Color(90, 93, 102));
                    strengthBar.setForeground(new Color(95, 89, 212));
                    return;
                }  
                int score = 0;
                if (p.length() >= 8) score++;
                if (p.matches(".*[a-z].*")) score++;
                if (p.matches(".*[A-Z].*")) score++;
                if (p.matches(".*[0-9].*")) score++;
                if (p.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;
                int pct = score * 20;
                strengthBar.setValue(pct);
                strengthBar.setString(pct + "%");
                resultLabel.setText("Strength: " + pct + "%");  
                if (score <= 2) {
                    resultLabel.setForeground(new Color(220, 78, 92));
                    strengthBar.setForeground(new Color(220, 78, 92));
                } else if (score <= 4) {
                    resultLabel.setForeground(new Color(227, 151, 52));
                    strengthBar.setForeground(new Color(227, 151, 52));
                } else {
                    resultLabel.setForeground(new Color(50, 179, 113));
                    strengthBar.setForeground(new Color(50, 179, 113));
                }
            }
        });
        mainPanel.add(formPanel);
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    private static class GradientPanel extends JPanel {
        private final Color start;
        private final Color end;

        GradientPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(true);
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            LinearGradientPaint paint = new LinearGradientPaint(
                    0f,
                    0f,
                    getWidth(),
                    getHeight(),
                    new float[]{0f, 0.55f, 1f},
                        new Color[]{start, new Color(28, 38, 72), end}
            );
            g2.setPaint(paint);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }
    private static class RoundedCardPanel extends JPanel {
        private final int arc;
        private final Color fill;

        RoundedCardPanel(int arc, Color fill) {
            this.arc = arc;
            this.fill = fill;
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(26, 28, 39, 28));
            g2.fillRoundRect(4, 10, getWidth() - 8, getHeight() - 8, arc, arc);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 12, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}