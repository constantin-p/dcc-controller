package dcccontroller.configuration.csv;

import java.util.HashMap;

public interface Storable {
    public HashMap<String, String> deconstruct();
    public static Storable construct(HashMap<String, String> valuesMap) {
        return null;
    }
}
