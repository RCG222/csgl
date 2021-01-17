import utils.Connect;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;

public class HOME  {
    private JButton test1Button;
    private JPanel panel1;
    private JButton button3;
    private static  Connection con = new Connect().loading();//打开主界面获取数据库连接；

    public HOME() {
        test1Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              ProductManagement productManagement = new ProductManagement();
            }
        });
        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Sell sell=new Sell();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("HOME");
        frame.setContentPane(new HOME().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocation(800,200);

    }

    //===========================================返回数据库连接

    public static Connection getHomeCoonnect(){
        return con;
    }



}
