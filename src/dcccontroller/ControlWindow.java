package dcccontroller;

import dcccontroller.model.CPDeviceItem;
import dcccontroller.serial.SerialCommunicationHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ControlWindow extends JFrame {

    private Application app;
    private CPDeviceItem device;

    public ControlWindow(Application app, CPDeviceItem device) {
        this.app = app;
        this.device = device;

        setTitle(Main.APP_NAME + " - (" + device.getName() + ")");
        createMenuBar();


        ControlPanel content = new ControlPanel();
        setContentPane(content.rootPanel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                System.out.println("Remove listeners");
                content.destroy(); // Remove listeners
            }
        });

        // Arrange the components inside the window
        pack();
        setSize(new Dimension(420, 520));

        setLocationRelativeTo(null);
        // By default, the window is not visible. Make it visible.
        setVisible(true);
    }


    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createToolsMenu());
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
            System.out.println("New Device");
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

    private JMenu createToolsMenu() {
        // Tools
        JMenu toolsMenu = new JMenu("Tools");

        toolsMenu.add(createPortsSubmenu());
        return toolsMenu;
    }

    private JMenu createPortsSubmenu() {
        JMenu menu = new JMenu("Port:");
        JMenuItem placeholderMenuItem = new JMenuItem("Serial Ports");
        placeholderMenuItem.setEnabled(false);

        ButtonGroup serialPorts = new ButtonGroup();

        JRadioButtonMenuItem radioAction1 = new JRadioButtonMenuItem(
                "Radio Button1");
        JRadioButtonMenuItem radioAction2 = new JRadioButtonMenuItem(
                "Radio Button2");
        serialPorts.add(radioAction1);
        serialPorts.add(radioAction2);



        JMenuItem refreshMenuItem = new JMenuItem("Refresh Ports");
        refreshMenuItem.addActionListener((ActionEvent event) -> {
            System.out.println("Refresh Ports");
            SerialCommunicationHelper.getInstance().searchPorts();
        });

        menu.add(placeholderMenuItem);
        menu.add(radioAction1);
        menu.add(radioAction2);
        menu.addSeparator();
        menu.add(refreshMenuItem);

        return menu;
    }

}
