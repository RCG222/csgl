import utils.Table_X;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class Sell {
    private JPanel panel1;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton 结算Button;
    private JButton 添加Button;
    private final int syyid=1;

    Table_X tx = new Table_X(table1,HOME.getHomeCoonnect());

    public Sell() throws SQLException {
        JFrame frame = new JFrame("Sell");
        frame.add(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocation(760,200);
        frame.setVisible(true);

        添加Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    tx.addRow(textField1.getText(),textField2.getText());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
        结算Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DoSell01 doSell01 = new DoSell01(tx,syyid);
            }
        });
    }

}
