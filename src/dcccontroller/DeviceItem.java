package dcccontroller;

import dcccontroller.model.CPDeviceItem;

import javax.swing.*;
import java.awt.*;

public class DeviceItem {
    public JPanel rootPanel;
    private JLabel nameLabel;
    private JLabel dateLabel;
    public JButton openButton;
    public JButton deleteButton;


    public CPDeviceItem device;

    public DeviceItem(CPDeviceItem device) {
        this.device = device;

        nameLabel.setText(device.getName());
        dateLabel.setText(device.getTimestamp());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        nameLabel = new JLabel();
        nameLabel.setForeground(Color.darkGray);
        dateLabel = new JLabel();
        dateLabel.setForeground(Color.gray);
    }
}
