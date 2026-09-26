package util;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;

public class UITheme {

    // =========================
    // MÀU SẮC
    // =========================
    public static final Color PRIMARY_DARK = Color.decode("#1261A6");
    public static final Color PRIMARY = Color.decode("#126DA6");
    public static final Color ACCENT = Color.decode("#2A95BF");
    public static final Color LIGHT = Color.decode("#73C6D9");
    public static final Color BACKGROUND = Color.decode("#EAF6FB");
    public static final Color WHITE = Color.WHITE;

    private UITheme() {
    }
    public static void apply(Container root) {
        root.setBackground(BACKGROUND);
        applyRecursive(root);
    }
    private static void applyRecursive(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setForeground(PRIMARY_DARK);
            }
            else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setBackground(PRIMARY);
                button.setForeground(WHITE);
            }
            else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setSelectionBackground(LIGHT);
                table.setSelectionForeground(Color.BLACK);
                table.setGridColor(ACCENT);
                JTableHeader header = table.getTableHeader();
                header.setBackground(PRIMARY_DARK);
                header.setForeground(WHITE);
            }
            else if (comp instanceof JTextField
                    || comp instanceof JPasswordField
                    || comp instanceof JTextArea) {
                comp.setBackground(WHITE);
                comp.setForeground(Color.BLACK);
            }
            else if (comp instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) comp;
                combo.setBackground(WHITE);
                combo.setForeground(Color.BLACK);
            } else if (comp instanceof JPanel) {
                comp.setBackground(BACKGROUND);
            }
            if (comp instanceof Container) {
                applyRecursive((Container) comp);
            }
        }
    }
}
