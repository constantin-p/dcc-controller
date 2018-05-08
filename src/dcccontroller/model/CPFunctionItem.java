package dcccontroller.model;

import dcccontroller.configuration.csv.Storable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CPFunctionItem implements Storable {
    public static List<String> COLUMN_VALUES = new ArrayList<>(Arrays.asList("command_display_name", "command"));
    private String displayName;
    private String command;

    public CPFunctionItem(String displayName, String command) {
        this.displayName = displayName;
        this.command = command;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCommand() {
        return command;
    }

    /*
     *  DB integration
     */
    @Override
    public HashMap<String, String> deconstruct() {
        HashMap<String, String> values = new HashMap<String, String>();

        values.put("command_display_name", this.displayName);
        values.put("command", this.command);

        return values;
    }

    public static CPFunctionItem construct(HashMap<String, String> valuesMap) {
        String displayName = valuesMap.get("command_display_name");
        String command = valuesMap.get("command");

        return new CPFunctionItem(displayName,command);
    }
}
