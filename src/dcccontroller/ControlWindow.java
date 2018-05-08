package dcccontroller;

import com.fazecast.jSerialComm.SerialPort;
import dcccontroller.model.CPDeviceItem;
import dcccontroller.serial.SerialCommunicationHelper;
import dcccontroller.util.Callback;
import dcccontroller.util.Change;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ControlWindow extends JFrame {

    private DialogManager dialogManager = new DialogManager(this);
    private CPDeviceItem device;
    private DeviceListWindow deviceListWindow;

    private ArrayList<Callback> destroyListeners = new ArrayList<>();

    public ControlWindow(DeviceListWindow deviceListWindow, CPDeviceItem device) {
        this.deviceListWindow = deviceListWindow;
        this.device = device;

        setTitle(Main.APP_NAME + " - (" + device.getName() + ")");
        createMenuBar();


        ControlPanel content = new ControlPanel(device);
        setContentPane(content.rootPanel);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                // Remove listeners
                content.destroy();
                destroy();
            }
        });

        // Arrange the components inside the window
        pack();
        setSize(new Dimension(420, 520));

        setLocationRelativeTo(null);
        // By default, the window is not visible. Make it visible.
        setVisible(true);
    }

    private void destroy() {
        destroyListeners.forEach((Callback listener) -> listener.call());
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
            deviceListWindow.createAndOpenDevice(dialogManager.showAddDeviceDialog());
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

        JMenu portsMenu = createPortsSubmenu();
        toolsMenu.add(portsMenu);

        SerialCommunicationHelper serialCommHelper = SerialCommunicationHelper.getInstance();
        Change<SerialPort> activePortChangeListener = (SerialPort activePort) -> {
            updatePortsSubmenu(portsMenu, serialCommHelper.getPorts());
        };
        Change<ArrayList<SerialPort>> portsChangeListener = (ArrayList<SerialPort> ports) -> {
            updatePortsSubmenu(portsMenu, ports);
        };

        serialCommHelper.addActivePortChangeListener(activePortChangeListener);
        serialCommHelper.addPortsChangeListener(portsChangeListener);

        destroyListeners.add(() -> {
            serialCommHelper.removeActivePortChangeListener(activePortChangeListener);
            serialCommHelper.removePortsChangeListener(portsChangeListener);
        });

        serialCommHelper.refreshPorts();
        return toolsMenu;
    }

    private JMenu createPortsSubmenu() {
        JMenu menu = new JMenu("Port:");
        JMenuItem placeholderMenuItem = new JMenuItem("Serial Ports: Loading...");
        placeholderMenuItem.setEnabled(false);


        JMenuItem refreshMenuItem = new JMenuItem("Refresh Ports");
        refreshMenuItem.addActionListener((ActionEvent event) -> {
            System.out.println("Refresh Ports");
            SerialCommunicationHelper.getInstance().refreshPorts();
        });

        menu.add(placeholderMenuItem);
        menu.addSeparator();
        menu.add(refreshMenuItem);

        return menu;
    }

    private void updatePortsSubmenu(JMenu menu, ArrayList<SerialPort> availablePorts) {
        SerialCommunicationHelper serialCommHelper = SerialCommunicationHelper.getInstance();

        menu.setText("Port:");
        JMenuItem placeholderMenuItem = new JMenuItem("Serial Ports");
        placeholderMenuItem.setEnabled(false);

        menu.removeAll();
        menu.add(placeholderMenuItem);

        ButtonGroup serialPorts = new ButtonGroup();
        availablePorts.forEach((SerialPort port) -> {
            JRadioButtonMenuItem portOption = new JRadioButtonMenuItem("["+ port.getSystemPortName() +"] " + port.getDescriptivePortName());
            if (serialCommHelper.portIsActive(port)) {
                portOption.setSelected(true);
                menu.setText("Port: \"" + port.getSystemPortName() + "\"");
            }
            serialPorts.add(portOption);
            menu.add(portOption);

            portOption.addItemListener((ItemEvent event) -> {
                if (event.getStateChange() == ItemEvent.SELECTED && !serialCommHelper.portIsActive(port)) {
                    serialCommHelper.setActivePort(port);
                }
            });
        });


        JMenuItem refreshMenuItem = new JMenuItem("Refresh Ports");
        refreshMenuItem.addActionListener((ActionEvent event) -> {
            System.out.println("Refresh Ports");
            SerialCommunicationHelper.getInstance().refreshPorts();
        });

        menu.addSeparator();
        menu.add(refreshMenuItem);
    }
}
