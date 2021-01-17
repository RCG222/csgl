import utils.Table_X;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class DoSell01 {
    private JPanel panel1;
    private JButton 结算Button;
    private JButton 取消Button;
    private JTextField textField4;
    private JTextField textField7;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    int syyid;
    Table_X tx;
    public DoSell01(Table_X tx,int syyid){
        this.tx=tx;
        this.syyid=syyid;
        JFrame frame = new JFrame("Sell");
        frame.add(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocation(760,200);
        frame.setVisible(true);

        textField1.setText(String.valueOf(syyid));
        textField2.setText(String.valueOf(tx.getYsje()));

        取消Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
        结算Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    tx.sell01(syyid,textField3.getText(),textField4.getText(),textField7.getText(),comboBox1.getSelectedIndex());
                    textField3.setText("");
                    textField4.setText("");
                    textField7.setText("");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}
