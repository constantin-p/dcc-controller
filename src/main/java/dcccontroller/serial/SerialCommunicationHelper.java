package dcccontroller.serial;

import com.fazecast.jSerialComm.SerialPort;
import dcccontroller.ErrorManager;
import dcccontroller.util.Change;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class SerialCommunicationHelper {
    private static SerialCommunicationHelper instance = null;

    private SerialPort[] ports = {};
    private SerialPort activePort = null;
    private final String COMMAND_PREFIX = "DCC_CTRL:";
    private final int BAUD_RATE = 9600;

    // Notification system
    private ArrayList<Change<ArrayList<SerialPort>>> portsChangeListeners = new ArrayList<>();
    private ArrayList<Change<SerialPort>> activePortChangeListeners = new ArrayList<>();

    private Thread listenThread;

    private SerialCommunicationHelper() {}

    public static SerialCommunicationHelper getInstance() {
        if (instance == null) {
            instance = new SerialCommunicationHelper();
        }
        return instance;
    }

    public void setActivePortFromPref(String portName) {
        refreshPorts();
        if (ports != null && portName != null) {
            for (int i = 0; i < ports.length; i++) {
                if (ports[i].getSystemPortName().equals(portName)) {
                    if (activePort == null || !activePort.getSystemPortName().equals(portName)) {
                        setActivePort(ports[i]);
                    }
                    break;
                }
            }
        }
    }

    public ArrayList<SerialPort> getPorts() {
        return new ArrayList<>(Arrays.asList(ports));
    }

    public SerialPort getActivePort() {
        return activePort;
    }

    public void setActivePort(SerialPort activePort) {
        if (this.activePort != null) {
            this.activePort.removeDataListener();
            this.activePort.closePort();
        }
        if (activePort != null) {
            activePort.setBaudRate(BAUD_RATE);
            activePort.openPort();
            listenForMessages(1);
        }
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

    public void sendCommand(String address, String command) {
        System.out.print("→ out :");
        String message = COMMAND_PREFIX + address + ":" + command;
        System.out.println(message);
        sendMessage(message);
    }

    private void sendMessage(String message) {
        if (activePort != null) {
            activePort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

            byte[] payload = (message + "\n").getBytes();
            try {
                activePort.writeBytes(payload, payload.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
            listenForMessages(3);
        } else {
            ErrorManager.showErrorMessage("Connection Error", "Port busy!");
        }
    }

    private void listenForMessages(int lines) {
        if (listenThread != null) {
            listenThread.interrupt();
        }
        listenThread = new Thread(){
            public void run(){
                receiveMessage(lines);
            }
        };

        listenThread.start();
    }

    private void receiveMessage(int lines) {
        if (activePort != null) {
            activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
            InputStream instr = activePort.getInputStream();
            if (instr != null) {
                Scanner in = new Scanner(instr);
                int count = 0;
                try {
                    while(count <= lines && in.hasNextLine()) {
                        String line = in.nextLine();
                        System.out.println("← in  :" + line);
                        count++;
                    }

                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Port busy!");
            }
        }
    }
}
