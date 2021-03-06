package dcccontroller;

import com.fazecast.jSerialComm.SerialPort;
import dcccontroller.configuration.Configuration;
import dcccontroller.configuration.ConfigurationManager;
import dcccontroller.model.CPConfigurationSelectItem;
import dcccontroller.model.CPFunctionItem;
import dcccontroller.serial.SerialCommunicationHelper;
import dcccontroller.util.Callback;
import dcccontroller.util.Change;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ControlPanel {
    public JPanel rootPanel;
    private JLabel displayFirstLine;
    private JLabel displaySecondLine;
    private JComboBox<CPConfigurationSelectItem> functionsComboBox;
    private JButton addConfigurationButton;
    private JTabbedPane speedTabbedPane;
    private JSlider reverseSpeedSlider;
    private JSlider forwardSpeedSlider;
    private JScrollPane functionsScrollPanel;
    private JLabel activePortLabel;

    private ConfigurationManager configurationManager = ConfigurationManager.getInstance();
    private SerialCommunicationHelper serialCommHelper = SerialCommunicationHelper.getInstance();
    private ControlWindow controlWindow;
    private ArrayList<Callback> destroyListeners = new ArrayList<>();


    private JLabel placeholderLabel;
    private JPanel contentPanel;
    private ArrayList<CPFunctionItem> functionList = new ArrayList<>();

    // Speed Cache
    private int cacheReverseSpeed = 0;
    private int cacheForwardSpeed = 0;

    public ControlPanel(ControlWindow controlWindow) {
        this.controlWindow = controlWindow;

        displayFirstLine.setText("Device: " + controlWindow.device.getName());
        setupPortLabel();
        setupSpeedSliders();
        setupConfigurations();

        addConfigurationButton.addActionListener((ActionEvent event) -> {
            controlWindow.dialogManager.showConfigurationsFilePicker((java.util.List<File> files) -> {
                configurationManager.importConfigurationFiles(files);
            });
        });
    }

    private void createUIComponents() {
        createConfigSelect();

        // Speed sliders
        createReverseSpeedSlider();
        createForwardSpeedSlider();

        // Functions pane
        createDefaultFunctionsPane();
    }

    public void destroy() {
        destroyListeners.forEach((Callback listener) -> listener.call());
    }

    // Helpers
    private void createConfigSelect() {
        functionsComboBox = new JComboBox<CPConfigurationSelectItem>();
        CPConfigurationSelectItem placeholder = new CPConfigurationSelectItem(" - Select a configuration - ");

        functionsComboBox.setModel(new DefaultComboBoxModel<CPConfigurationSelectItem>() {
            boolean selectPlaceholder = true;

            @Override
            public void setSelectedItem(Object anObject) {
                CPConfigurationSelectItem item = (CPConfigurationSelectItem) anObject;
                if (item.isSelectable()) {
                    setupFunctionsPane(item.getConfiguration());
                    super.setSelectedItem(anObject);
                } else if (selectPlaceholder) {
                    // Allow this just once
                    selectPlaceholder = false;
                    super.setSelectedItem(anObject);
                }
            }
        });

        functionsComboBox.addItem(placeholder);
    }

    private void createReverseSpeedSlider() {
        // Speed Slider
        reverseSpeedSlider = new JSlider(0, 15);
        reverseSpeedSlider.setMinorTickSpacing(1);
        reverseSpeedSlider.setMajorTickSpacing(5);
        reverseSpeedSlider.setPaintTicks(true);
        reverseSpeedSlider.setSnapToTicks(true);
        reverseSpeedSlider.setValue(0);

        // Labels
        Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();

        JLabel minLabel = new JLabel("Min");
        JLabel maxLabel = new JLabel("Max");

        Font labelFont = new Font("Monospaced",  minLabel.getFont().getStyle(),  minLabel.getFont().getSize());
        minLabel.setFont(labelFont);
        maxLabel.setFont(labelFont);
        minLabel.setForeground(Color.darkGray);
        maxLabel.setForeground(Color.darkGray);

        labelTable.put(0, minLabel);
        labelTable.put(15, maxLabel);

        reverseSpeedSlider.setPaintLabels(true);
        reverseSpeedSlider.setLabelTable(labelTable);
    }

    private void createForwardSpeedSlider() {
        // Speed Slider
        forwardSpeedSlider = new JSlider(0, 15);
        forwardSpeedSlider.setMinorTickSpacing(1);
        forwardSpeedSlider.setMajorTickSpacing(5);
        forwardSpeedSlider.setPaintTicks(true);
        forwardSpeedSlider.setSnapToTicks(true);
        forwardSpeedSlider.setValue(0);

        // Labels
        Dictionary<Integer, Component> labelTable = new Hashtable<Integer, Component>();

        JLabel minLabel = new JLabel("Min");
        JLabel maxLabel = new JLabel("Max");

        Font labelFont = new Font("Monospaced",  minLabel.getFont().getStyle(),  minLabel.getFont().getSize());
        minLabel.setFont(labelFont);
        maxLabel.setFont(labelFont);
        minLabel.setForeground(Color.darkGray);
        maxLabel.setForeground(Color.darkGray);

        labelTable.put(0, minLabel);
        labelTable.put(15, maxLabel);

        forwardSpeedSlider.setPaintLabels(true);
        forwardSpeedSlider.setLabelTable(labelTable);
    }

    private void createDefaultFunctionsPane() {
        functionsScrollPanel = new JScrollPane();
        functionsScrollPanel.setOpaque(false);
        functionsScrollPanel.getViewport().setOpaque(false);
        functionsScrollPanel.setBorder(null);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        placeholderLabel = new JLabel("No configuration selected");
        placeholderLabel.setForeground(Color.darkGray);
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        showEmptyPlaceholder();
    }

    private void setupFunctionsPane(Configuration configuration) {
        contentPanel.removeAll();
        if (functionList.isEmpty()) {
            functionsScrollPanel.setViewportView(contentPanel);
        }
        functionList = new ArrayList<CPFunctionItem>(configuration.getFunctions());

        for (int i = 0; i < functionList.size(); i++) {
            CPFunctionItem item = functionList.get(i);
            FunctionItem functionItem = new FunctionItem(item, i);
            functionItem.functionButton.addActionListener((ActionEvent event) -> {
                serialCommHelper.sendCommand(controlWindow.device.getName(), item.getCommand());
            });

            contentPanel.add(functionItem.rootPanel);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEmptyPlaceholder() {
        functionsScrollPanel.getViewport().add(placeholderLabel);
    }

    private void setupConfigurations() {
        Change<ArrayList<Configuration>> configurationsChangeListener = (ArrayList<Configuration> configurations) -> {
            ArrayList<Configuration> configs = new ArrayList<>(configurations);
            for (int i = 0; i < functionsComboBox.getItemCount(); i++) {
                CPConfigurationSelectItem item = functionsComboBox.getItemAt(i);
                if (item.isSelectable()) {
                    if (configs.contains(item.getConfiguration())) {
                        configs.remove(item.getConfiguration());
                    } else {
                        functionsComboBox.removeItem(item);
                        i--;
                    }
                }
            }

            configs.forEach(configuration -> {
                functionsComboBox.addItem(new CPConfigurationSelectItem(configuration));
            });
        };

        ArrayList<Configuration> configurations = configurationManager.getConfigurations();
        if (!configurations.isEmpty()) {
            configurationsChangeListener.call(configurations);
        }
        configurationManager.addConfigurationsChangeListener(configurationsChangeListener);

        destroyListeners.add(() -> {
            configurationManager.removeConfigurationsChangeListener(configurationsChangeListener);
        });
    }

    private void setupPortLabel() {
        Change<SerialPort> activePortChangeListener = (SerialPort activePort) -> {
            if (activePort == null) {
                activePortLabel.setText("No Serial Port selected");
            } else {
                activePortLabel.setText("Serial Port: \"" + activePort.getSystemPortName() + "\"");
            }
        };

        SerialPort activePort = serialCommHelper.getActivePort();
        if (activePort != null) {
            activePortChangeListener.call(activePort);
        }

        serialCommHelper.addActivePortChangeListener(activePortChangeListener);

        destroyListeners.add(() -> {
            serialCommHelper.removeActivePortChangeListener(activePortChangeListener);
        });
    }

    private void setupSpeedSliders() {
        // Reverse
        reverseSpeedSlider.addChangeListener((ChangeEvent event) -> {
            int currentValue = reverseSpeedSlider.getValue();
            if (cacheReverseSpeed != currentValue) {
                cacheReverseSpeed = currentValue;


                if (!isForwardSelected()) {
                    if (forwardSpeedSlider.getValue() != 0) {
                        forwardSpeedSlider.setValue(0);
                    }
                    serialCommHelper.sendCommand(controlWindow.device.getName(), "REVERSE:" + currentValue);
                    displaySecondLine.setText("Speed: " + (currentValue == 0 ? "-": "(Reverse) " + currentValue));
                }
            }
        });
        // Forward
        forwardSpeedSlider.addChangeListener((ChangeEvent event) -> {
            int currentValue = forwardSpeedSlider.getValue();
            if (cacheForwardSpeed != currentValue) {
                cacheForwardSpeed = currentValue;


                if (isForwardSelected()) {
                    if (reverseSpeedSlider.getValue() != 0) {
                        reverseSpeedSlider.setValue(0);
                    }
                    serialCommHelper.sendCommand(controlWindow.device.getName(), "FORWARD:" + currentValue);
                    displaySecondLine.setText("Speed: " + (currentValue == 0 ? "-": "(Forward) " + currentValue));
                }
            }
        });
    }

    private boolean isForwardSelected() {
        return speedTabbedPane.getSelectedIndex() == 1; // 0 - Reverse, 1 - Forward
    }
}
