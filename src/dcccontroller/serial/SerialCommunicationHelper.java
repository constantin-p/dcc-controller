package dcccontroller.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Arrays;

public class SerialCommunicationHelper {
    private static SerialCommunicationHelper instance = null;

    private ArrayList<SerialPort> ports = new ArrayList<>();
    private SerialPort currentPort = null;

    // Notification system
    private ArrayList<Change<ArrayList<SerialPort>>> portsChangeListeners = new ArrayList<>();
    private ArrayList<Change<SerialPort>> currentPortChangeListeners = new ArrayList<>();

    interface Change<T> {
        void call(T input);
    }

    private SerialCommunicationHelper() {}

    public static SerialCommunicationHelper getInstance() {
        if (instance == null) {
            instance = new SerialCommunicationHelper();
        }
        return instance;
    }

    public ArrayList<SerialPort> getPorts() {
        return ports;
    }

    public void addPortsChangeListener(Change<ArrayList<SerialPort>> listener) {
        portsChangeListeners.add(listener);
    }
    public void removePortsChangeListener(Change<ArrayList<SerialPort>> listener) {
        portsChangeListeners.remove(listener);
    }

    public void addCurrentPortChangeListener(Change<SerialPort> listener) {
        currentPortChangeListeners.add(listener);
    }
    public void removeCurrentPortChangeListener(Change<SerialPort> listener) {
        currentPortChangeListeners.remove(listener);
    }

    public void searchPorts() {
        ports.clear();
        ports.addAll(Arrays.asList(SerialPort.getCommPorts()));
        ports.forEach((SerialPort port) -> {
            System.out.println(port.getSystemPortName());
            System.out.println(port.getDescriptivePortName());
            System.out.println(port.getPortDescription());
        });
    }
}
