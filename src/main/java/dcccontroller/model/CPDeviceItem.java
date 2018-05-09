package dcccontroller.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CPDeviceItem implements Serializable {
    private String name;
    private String timestamp;

    public CPDeviceItem(String name, String timestamp) {
        this.name = name.trim();
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static String generateTimestamp(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return DateTimeFormatter.ofPattern("yyyy.MM.dd").format(date);
    }

    public static String generateTimestamp() {
        return CPDeviceItem.generateTimestamp(null);
    }
}
