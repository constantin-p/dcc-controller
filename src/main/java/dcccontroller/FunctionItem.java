package dcccontroller;

import dcccontroller.model.CPFunctionItem;

import javax.swing.*;
import java.awt.*;

public class FunctionItem {
    public JPanel rootPanel;
    private JLabel functionLabel;
    public JButton functionButton;

    private CPFunctionItem function;
    private int index;

    public FunctionItem(CPFunctionItem function, int index) {
        this.function = function;
        this.index = index;

        functionLabel.setText("F" + index + ": " + function.getDisplayName());
        functionButton.setText("F" + index);
    }

    private void createUIComponents() {

        functionLabel = new JLabel();
        functionLabel.setForeground(Color.darkGray);
        functionButton = new JButton();
        functionButton.setForeground(Color.black);
    }
}
