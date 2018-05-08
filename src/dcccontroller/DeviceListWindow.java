package dcccontroller;

import dcccontroller.model.CPDeviceItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DeviceListWindow extends JFrame {

    private Application app;
    private DialogManager dialogManager = new DialogManager(this);
    private DeviceList content;

    public DeviceListWindow(Application app) {
        this.app = app;

        setTitle(Main.APP_NAME + " - Device list");
        createMenuBar();

        ArrayList<CPDeviceItem> deviceItems = app.getDevices();

        content = new DeviceList(deviceItems, app, dialogManager, (ArrayList<CPDeviceItem> item) -> {
            // deviceListChangeCallback
            app.setDevices(item);
        });
        setContentPane(content.rootPanel);

        // Arrange the components inside the window
        pack();
        setSize(new Dimension(360, 420));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // By default, the window is not visible. Make it visible.
        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        setJMenuBar(menuBar);
    }

    private JMenu createFileMenu() {
        // File
        JMenu fileMenu = new JMenu("File");

        // New Device
        JMenuItem newDeviceMenuItem = new JMenuItem("New Device");
        newDeviceMenuItem.setMnemonic(KeyEvent.VK_N);
        newDeviceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newDeviceMenuItem.addActionListener((ActionEvent event) -> {

                String name = dialogManager.showAddDeviceDialog();
                if (name != null) {
                    content.addDeviceItem(new CPDeviceItem(name, CPDeviceItem.generateTimestamp()));
                }
        });

        // Import configuration
        JMenuItem importConfigMenuItem = new JMenuItem("Import configuration");
        importConfigMenuItem.addActionListener((ActionEvent event) -> {
            System.out.println("Import configuration");
        });


        fileMenu.add(newDeviceMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(importConfigMenuItem);
        return fileMenu;
    }
}
