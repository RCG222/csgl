import Dao.ProductDao;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewProduct {
    int flag = 0;
    private JPanel panel1;
    private JButton 添加Button;
    private JButton 取消Button;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField7;
    private JTextField textField8;
    private ResultSet query;
    ProductDao productDao = new ProductDao();//创建连接；
    public NewProduct() {


        JFrame frame = new JFrame("ProductManagement");
        frame.add(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocation(500,450);
        frame.setVisible(true);


        添加Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    int z = 0;
                    query = productDao.query(HOME.getHomeCoonnect(), 0, (String) textField1.getText(), (String) textField2.getText(), (String) textField3.getText(), (String) textField4.getText(), Double.parseDouble(textField5.getText()), Double.parseDouble(textField8.getText()),z);

                } catch (SQLException ex) {

                }
                    JOptionPane.showMessageDialog(panel1, "添加成功","商品添加",1);
                    textField1.setText("");
                    textField2.setText("");
                    textField3.setText("");
                    textField4.setText("");
                    textField5.setText("");
                    textField7.setText("");
                    textField8.setText("");


            }
        });



        取消Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flag = 1;
                frame.dispose();
            }
        });
    }

}
