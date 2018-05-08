package dcccontroller;

import com.fazecast.jSerialComm.SerialPort;
import dcccontroller.model.CPSelectItem;
import dcccontroller.serial.SerialCommunicationHelper;
import dcccontroller.util.Callback;
import dcccontroller.util.Change;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ControlPanel {
    public JPanel rootPanel;
    private JLabel displayFirstLine;
    private JLabel displaySecondLine;
    private JComboBox functionsComboBox;
    private JButton addConfigurationButton;
    private JTabbedPane tabbedPane1;
    private JSlider reverseSpeedSlider;
    private JSlider forwardSpeedSlider;
    private JScrollPane functionsScrollPanel;
    private JLabel activePortLabel;

    private ArrayList<Callback> destroyListeners = new ArrayList<>();

    public ControlPanel() {
        SerialCommunicationHelper serialCommHelper = SerialCommunicationHelper.getInstance();

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

    private void createUIComponents() {
        createConfigSelect();
        functionsComboBox.addItem("1");
        functionsComboBox.addItem("2");

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
        functionsComboBox = new JComboBox<CPSelectItem>();
        CPSelectItem placeholder = new CPSelectItem("Placeholder", " - Select a configuration - ", false);

        functionsComboBox.setModel(new DefaultComboBoxModel<String>() {
            boolean selectPlaceholder = true;

            @Override
            public void setSelectedItem(Object anObject) {
                if (!(anObject instanceof CPSelectItem) || ((CPSelectItem) anObject).isSelectable()) {
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
        reverseSpeedSlider = new JSlider(0, 128);
        reverseSpeedSlider.setMinorTickSpacing(4);
        reverseSpeedSlider.setMajorTickSpacing(32);
        reverseSpeedSlider.setPaintTicks(true);
        reverseSpeedSlider.setSnapToTicks(true);

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
        labelTable.put(128, maxLabel);

        reverseSpeedSlider.setPaintLabels(true);
        reverseSpeedSlider.setLabelTable(labelTable);
    }

    private void createForwardSpeedSlider() {
        // Speed Slider
        forwardSpeedSlider = new JSlider(0, 128);
        forwardSpeedSlider.setMinorTickSpacing(4);
        forwardSpeedSlider.setMajorTickSpacing(32);
        forwardSpeedSlider.setPaintTicks(true);
        forwardSpeedSlider.setSnapToTicks(true);

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
        labelTable.put(128, maxLabel);

        forwardSpeedSlider.setPaintLabels(true);
        forwardSpeedSlider.setLabelTable(labelTable);
    }

    private void createDefaultFunctionsPane() {
        functionsScrollPanel = new JScrollPane();
        functionsScrollPanel.setOpaque(false);
        functionsScrollPanel.getViewport().setOpaque(false);
        functionsScrollPanel.setBorder(null);

        JLabel placeholderLabel = new JLabel("No configuration selected");
        placeholderLabel.setForeground(Color.darkGray);
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        functionsScrollPanel.getViewport().add(placeholderLabel);
    }

    private void createFunctionsPane() {

    }
}
