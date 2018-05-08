package dcccontroller.serial;

import com.fazecast.jSerialComm.SerialPort;
import dcccontroller.util.Change;

import java.util.ArrayList;
import java.util.Arrays;

public class SerialCommunicationHelper {
    private static SerialCommunicationHelper instance = null;

    private SerialPort[] ports = {};
    private SerialPort activePort = null;

    // Notification system
    private ArrayList<Change<ArrayList<SerialPort>>> portsChangeListeners = new ArrayList<>();
    private ArrayList<Change<SerialPort>> activePortChangeListeners = new ArrayList<>();


    private SerialCommunicationHelper() {}

    public static SerialCommunicationHelper getInstance() {
        if (instance == null) {
            instance = new SerialCommunicationHelper();
        }
        return instance;
    }

    public ArrayList<SerialPort> getPorts() {
        return new ArrayList<>(Arrays.asList(ports));
    }

    public SerialPort getActivePort() {
        return activePort;
    }

    public void setActivePort(SerialPort activePort) {
        this.activePort = activePort;
        activePortChangeListeners.forEach((Change<SerialPort> listener) -> listener.call(activePort));
    }

    public boolean portIsActive(SerialPort port) {
        return (activePort != null && port != null && port.getDescriptivePortName().equals(activePort.getDescriptivePortName()));
    }

    public void addPortsChangeListener(Change<ArrayList<SerialPort>> listener) {
        portsChangeListeners.add(listener);
    }
    public void removePortsChangeListener(Change<ArrayList<SerialPort>> listener) {
        portsChangeListeners.remove(listener);
    }

    public void addActivePortChangeListener(Change<SerialPort> listener) {
        activePortChangeListeners.add(listener);
    }
    public void removeActivePortChangeListener(Change<SerialPort> listener) {
        activePortChangeListeners.remove(listener);
    }

    public void refreshPorts() {
        boolean activePortExists = false;
        ports = SerialPort.getCommPorts();
        portsChangeListeners.forEach((Change<ArrayList<SerialPort>> listener) -> listener.call(getPorts()));

        for (SerialPort port: ports) {
            if (activePort != null && port.getDescriptivePortName().equals(activePort.getDescriptivePortName())) {
                activePortExists = true;
                break;
            }
        }

        if (!activePortExists) {
            setActivePort(null);
        }
    }
}
