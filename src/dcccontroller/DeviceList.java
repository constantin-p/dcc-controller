package dcccontroller;

import dcccontroller.model.CPDeviceItem;
import dcccontroller.util.Change;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class DeviceList {
    public JPanel rootPanel;
    private JButton addDeviceButton;
    private JLabel menuInstructionLabel;
    private JLabel orLabel;
    private JScrollPane deviceScrollPane;

    private JLabel placeholderLabel;
    private JPanel contentPanel;
    private ArrayList<CPDeviceItem> deviceList = new ArrayList<>();

    private Application app;
    private Change<ArrayList<CPDeviceItem>> deviceListChangeCallback;
    private DialogManager dialogManager;


    public DeviceList(ArrayList<CPDeviceItem> deviceItems, Application app, DialogManager dialogManager, Change<ArrayList<CPDeviceItem>> deviceListChangeCallback) {
        this.app = app;
        this.dialogManager = dialogManager;
        this.deviceListChangeCallback = deviceListChangeCallback;

        if (!deviceItems.isEmpty()) {
            if (deviceList.isEmpty()) {
                deviceScrollPane.setViewportView(contentPanel);
            }
            deviceItems.forEach((CPDeviceItem item) -> {
                deviceList.add(item);
                DeviceItem deviceItem = new DeviceItem(item);
                addDeviceItemListeners(deviceItem);
                contentPanel.add(deviceItem.rootPanel);
            });
            contentPanel.revalidate();
            contentPanel.repaint();
        }

        addDeviceButton.addActionListener((ActionEvent event) -> {
            String name = dialogManager.showAddDeviceDialog();
            if (name != null) {
                addDeviceItem(new CPDeviceItem(name, CPDeviceItem.generateTimestamp()));
            }
        });
    }

    private void createUIComponents() {
        menuInstructionLabel = new JLabel();
        menuInstructionLabel.setForeground(Color.darkGray);
        orLabel = new JLabel();
        orLabel.setForeground(Color.gray);
        // Devices pane
        createDefaultDeviceListPane();
    }

    private void createDefaultDeviceListPane() {

        deviceScrollPane = new JScrollPane();
        deviceScrollPane.setOpaque(false);
        deviceScrollPane.getViewport().setOpaque(false);
        deviceScrollPane.setBorder(null);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        placeholderLabel = new JLabel("No devices");
        placeholderLabel.setForeground(Color.darkGray);
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        showEmptyPlaceholder();
    }

    public void addDeviceItem(CPDeviceItem item) {
        if (deviceList.isEmpty()) {
            deviceScrollPane.setViewportView(contentPanel);
        }

        deviceList.add(item);
        deviceListChangeCallback.call(deviceList);

        DeviceItem deviceItem = new DeviceItem(item);
        addDeviceItemListeners(deviceItem);

        contentPanel.add(deviceItem.rootPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addDeviceItemListeners(DeviceItem deviceItem) {
        deviceItem.deleteButton.addActionListener((ActionEvent event) -> {
            if (dialogManager.showRemoveDeviceDialog(deviceItem.device.getName())) {
                deviceList.remove(deviceItem.device);
                deviceListChangeCallback.call(deviceList);
                app.hideControlWindow(deviceItem.device);

                contentPanel.remove(deviceItem.rootPanel);
                contentPanel.revalidate();
                contentPanel.repaint();

                if (deviceList.isEmpty()) {
                    showEmptyPlaceholder();
                }
            }
        });
        deviceItem.openButton.addActionListener((ActionEvent event) -> {
            app.showControlWindow(deviceItem.device);
        });
    }

    private void showEmptyPlaceholder() {
        deviceScrollPane.getViewport().add(placeholderLabel);
    }
}
