package dcccontroller.model;

public class CPFunctionItem {
    private String value;
    private String displayName;
    private int order;

    public CPFunctionItem(String value, String displayName, int order) {
        this.value = value;
        this.displayName = displayName;
        this.order = order;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }
}
