package dcccontroller.util;

public interface Change<T> {
    void call(T input);
}