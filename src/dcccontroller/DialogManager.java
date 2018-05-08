package dcccontroller;

import dcccontroller.model.CPDeviceItem;

import javax.swing.*;
import java.awt.*;

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

        System.out.println(result);
        return (result == JOptionPane.NO_OPTION); // NO_OPTION == Delete
    }
}
