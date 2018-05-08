package dcccontroller.model;

import dcccontroller.configuration.Configuration;

public class CPConfigurationSelectItem {
    private Configuration configuration;
    private String displayName;
    private boolean isSelectable = true;

    public CPConfigurationSelectItem(Configuration configuration) {
        this.configuration = configuration;
    }

    public CPConfigurationSelectItem(String displayName) {
        this.displayName = displayName;
        this.isSelectable = false;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        if (configuration == null) {
            return displayName;
        }
        return configuration.getName();
    }
}
