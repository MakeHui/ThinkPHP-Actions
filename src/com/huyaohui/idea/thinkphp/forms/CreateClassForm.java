package com.huyaohui.idea.thinkphp.forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CreateClassForm extends JDialog {
    private JTextField textField1;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel contentPane;

    private DialogCallBack mCallBack;

    /**
     * 在自动创建该类的时候，添加一个回调函数DialogCallBack，并且改变了onOK这个方法
     * @param callBack 回调函数
     */
    public CreateClassForm(DialogCallBack callBack) {
        this.mCallBack = callBack;
        setTitle("Create Class");
        setContentPane(contentPane);
        setModal(true);
        setSize(new Dimension(348, 120));
        setLocationRelativeTo(null);
        setResizable(false);
        getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        if (null != mCallBack){
            mCallBack.ok(textField1.getText().trim());
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    // 这个作废，去掉，无用
//    public static void main(String[] args) {
//        MvpAutomaticCreation dialog = new MvpAutomaticCreation();
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    public interface DialogCallBack{
        void ok(String className);
    }

}
