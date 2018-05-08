package dcccontroller;

import dcccontroller.model.CPDeviceItem;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class Application {
    private Preferences prefs = Preferences.userNodeForPackage(Application.class);
    private final String PREF_DEVICE_LIST_KEY = "PREF_DEVICE_LIST_KEY";

    private Map<CPDeviceItem, ControlWindow> deviceWindowMap = new HashMap<>();

    public Application() {
        SwingUtilities.invokeLater(() -> {
            new DeviceListWindow(this);
        });
    }

    public void showControlWindow(CPDeviceItem item) {
        if (deviceWindowMap.containsKey(item)) {
            deviceWindowMap.get(item).toFront();
        } else {
            SwingUtilities.invokeLater(() -> {
                deviceWindowMap.put(item, new ControlWindow(this, item));
            });
        }
    }

    public void hideControlWindow(CPDeviceItem item) {
        if (deviceWindowMap.containsKey(item)) {
            ControlWindow window = deviceWindowMap.get(item);
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            deviceWindowMap.remove(item);
        }
    }

    public void setDevices(ArrayList<CPDeviceItem> items) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(items);
            oos.flush();
            setPreferenceAsByteArray(PREF_DEVICE_LIST_KEY, baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("setDevices: " + e.getMessage());
        }
    }

    public ArrayList<CPDeviceItem> getDevices() {
        try {
            byte[] input = getPreferenceAsByteArray(PREF_DEVICE_LIST_KEY);
            ByteArrayInputStream bais = new ByteArrayInputStream(input);
            ObjectInputStream ois = new ObjectInputStream(bais);

            Object result = ois.readObject();
            return (ArrayList<CPDeviceItem>) result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getDevices: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // Helpers
    private void setPreferenceAsByteArray(String key, byte[] array) {
        prefs.putByteArray(key, array);
    }
    private void setPreferenceAsString(String key, String value) {
        prefs.put(key, value);
    }

    private byte[] getPreferenceAsByteArray(String key) {
        return prefs.getByteArray(key, null);
    }
    private String getPreferenceAsString(String key) {
        return prefs.get(key, null);
    }
}