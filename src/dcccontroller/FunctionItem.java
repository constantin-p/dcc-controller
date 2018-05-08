package dcccontroller;

import dcccontroller.model.CPFunctionItem;

import javax.swing.*;

public class FunctionItem {
    private JPanel rootPanel;
    private JLabel functionLabel;
    private JButton functionButton;

    private CPFunctionItem function;

    public FunctionItem(CPFunctionItem function) {
        this.function = function;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        functionLabel = new JLabel(function.getDisplayName());
        functionButton = new JButton("F" + function.getOrder());
    }
}
