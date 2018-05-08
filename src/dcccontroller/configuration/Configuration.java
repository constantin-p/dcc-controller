package dcccontroller.configuration;

import dcccontroller.model.CPFunctionItem;

import java.io.Serializable;
import java.util.List;

public class Configuration implements Serializable {
    private String name;
    private List<CPFunctionItem> functions;

    public Configuration(String name, List<CPFunctionItem> functions) {
        this.name = name;
        this.functions = functions;
    }

    public String getName() {
        return name;
    }

    public List<CPFunctionItem> getFunctions() {
        return functions;
    }
}
