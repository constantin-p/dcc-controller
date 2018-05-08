package dcccontroller;

import javax.swing.*;

public class Main {
    static String APP_NAME = "DCC Controller";

    public static void main(String[] args) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
        } catch(InstantiationException e) {
            System.out.println("InstantiationException: " + e.getMessage());
        } catch(IllegalAccessException e) {
            System.out.println("IllegalAccessException: " + e.getMessage());
        } catch(UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }

        new Application();
    }
}
