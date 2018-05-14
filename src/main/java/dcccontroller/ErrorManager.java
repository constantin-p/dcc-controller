package dcccontroller;

import javax.swing.*;

public class ErrorManager {

    static public void showErrorMessage(String title, String msg) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
        });
    }
}
