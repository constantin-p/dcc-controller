package dcccontroller;

import com.sun.javafx.application.PlatformImpl;
import dcccontroller.util.Change;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class DialogManager {
    private Frame frame;

    public DialogManager(Frame frame) {
        this.frame = frame;
    }

    public String showAddDeviceDialog() {
        String deviceName = JOptionPane.showInputDialog(
                frame,
                "Enter the DCC Addresses",
                "Create Device",
                JOptionPane.PLAIN_MESSAGE
        );

        if (deviceName != null && !deviceName.trim().isEmpty()) {
            return deviceName;
        }
        return null;
    }

    public boolean showRemoveDeviceDialog(String deviceName) {
        String[] options = {"Cancel", "Delete"};
        int result = JOptionPane.showOptionDialog(
                frame,
                "Are you sure you want to delete device " + deviceName + " ?",
                "Delete Device: " + deviceName,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                JOptionPane.NO_OPTION
        );

        return (result == JOptionPane.NO_OPTION); // NO_OPTION == Delete
    }

    public void showConfigurationFileTemplate() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Configuration file example:");
        label.setForeground(Color.darkGray);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea textarea = new JTextArea("\"command_display_name\",\"command\"\n\"Bell\",\"BELL\"");
        textarea.setEditable(false);
        textarea.setFont(new Font("Monospaced",  textarea.getFont().getStyle(),  textarea.getFont().getSize()));
        textarea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel messageLabel = new JLabel("Save as a CSV file (*.csv)");
        messageLabel.setForeground(Color.darkGray);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(textarea);
        panel.add(Box.createVerticalStrut(10));
        panel.add(messageLabel);

        String[] options = {"Cancel"};
        JOptionPane.showOptionDialog(
                frame,
                panel,
                "Configuration file example",
                JOptionPane.CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
    }

    public void showConfigurationsFilePicker(Change<java.util.List<File>> onDone) {
        PlatformImpl.startup(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Configuration Files");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Comma-separated values (CSV) files", "*.csv")
            );

            java.util.List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                onDone.call(selectedFiles);
            } else {
                onDone.call(null);
            }
        });
    }
}
