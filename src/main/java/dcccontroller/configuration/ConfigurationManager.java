package dcccontroller.configuration;


import dcccontroller.ErrorManager;
import dcccontroller.configuration.csv.CSVFileHandler;
import dcccontroller.model.CPFunctionItem;
import dcccontroller.util.Change;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationManager {
    private static String CONFIGURATION_NAME_REGEXP = "[^A-Za-z0-9-_.]+";
    private static ConfigurationManager instance = null;

    private ArrayList<Configuration> configurations = new ArrayList<>();
    // Notification system
    private ArrayList<Change<ArrayList<Configuration>>> configurationsChangeListeners = new ArrayList<>();

    private ConfigurationManager() { }


    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public ArrayList<Configuration> getConfigurations() {
        return configurations;
    }

    public void addConfigurationsChangeListener(Change<ArrayList<Configuration>> listener) {
        configurationsChangeListeners.add(listener);
    }
    public void removeConfigurationsChangeListener(Change<ArrayList<Configuration>> listener) {
        configurationsChangeListeners.remove(listener);
    }

    // Import
    public void importConfigurationFiles(List<File> files) {
        if (files != null) {
            files.forEach((File file) -> {
                if (file != null) {
                    Configuration config = createConfiguration(file);
                    if (config != null) {
                        configurations.add(config);
                        configurationsChangeListeners.forEach(listener -> listener.call(configurations));
                    }
                }
            });
        }
    }


    public Configuration createConfiguration(File file) {
        List<HashMap<String, String>> entries;
        try {
            entries = getAll(file.toPath());
        } catch (IllegalArgumentException e) {

            showErrorMessage(e);
            return null;
        }

        List<CPFunctionItem> functions = new ArrayList<CPFunctionItem>();
        for (HashMap<String, String> entry : entries) {
            functions.add(CPFunctionItem.construct(entry));
        }

        return new Configuration(createConfigName(file), functions);
    }

    // https://github.com/constantin-p/dat16j-exam-project/blob/master/src/examproject/db/TableHandler.java
    private List<HashMap<String, String>> getAll(Path filePath) {
        List<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            checkColumnValues(lines.get(0));
            lines.stream()
                .skip(1)
                .forEach(line -> {
                    HashMap<String, String> rowValueMap = this.mapRowValues(line);
                    if (rowValueMap != null) {
                        rows.add(rowValueMap);
                    }
                });

        } catch (IOException e) {
            showErrorMessage(e);
            e.printStackTrace();
        }
        return rows;
    }

    private void checkColumnValues(String line) {
        try {
            List<String> values = CSVFileHandler.parseLine(new StringReader(line));

            //  Check if the row contains the right amount of values
            if (CPFunctionItem.COLUMN_VALUES.size() != values.size()) {
                throw new Exception("Unexpected row values count (" + values.size()
                        + " instead of " + CPFunctionItem.COLUMN_VALUES.size() + ")");
            }

            // Map the values to the column names
            for (int i = 0; i < CPFunctionItem.COLUMN_VALUES.size(); i++) {
                if (!CPFunctionItem.COLUMN_VALUES.get(i).equals(values.get(i))) {
                    throw new Exception("Unexpected column key (\"" + values.get(i)
                            + "\" instead of \"" + CPFunctionItem.COLUMN_VALUES.get(i) + "\")");
                }
            }

        } catch (Exception e) {
            showErrorMessage(e);
            e.printStackTrace();
        }
    }

    private HashMap<String, String> mapRowValues(String line) {
        try {
            List<String> values = CSVFileHandler.parseLine(new StringReader(line));
            HashMap<String, String> rowValueMap = new HashMap<String, String>();

            //  Check if the row contains the right amount of values
            if (CPFunctionItem.COLUMN_VALUES.size() != values.size()) {
                throw new Exception("Unexpected row values count (" + values.size()
                        + " instead of " + CPFunctionItem.COLUMN_VALUES.size() + ")");
            }

            // Map the values to the column names
            for (int i = 0; i < CPFunctionItem.COLUMN_VALUES.size(); i++) {
                rowValueMap.put(CPFunctionItem.COLUMN_VALUES.get(i), values.get(i));
            }

            return rowValueMap;
        } catch (Exception e) {
            showErrorMessage(e);
            e.printStackTrace();
        }
        return null;
    }

    private void showErrorMessage(Exception e) {
        ErrorManager.showErrorMessage("Configuration Error", "Could not open file \n" + e.getMessage());
    }

    private String createConfigName(File file) {
        String name = file.getName();
        if (name.isEmpty()) {
            return "< untitled >";
        }

        int pos = name.lastIndexOf(".");
        if (pos >= 0) {
            name = name.substring(0, pos);
        }
        return name.replaceAll(CONFIGURATION_NAME_REGEXP, "_").trim();
    }
}
