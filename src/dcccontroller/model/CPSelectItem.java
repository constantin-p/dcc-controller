package dcccontroller.model;

public class CPSelectItem {
    private String value;
    private String displayName;
    private boolean isSelectable = true;

    public CPSelectItem(String value) {
        this.value = value;
        this.displayName = value;
    }

    public CPSelectItem(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public CPSelectItem(String value, String displayName, boolean isSelectable) {
        this.value = value;
        this.displayName = displayName;
        this.isSelectable = isSelectable;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
