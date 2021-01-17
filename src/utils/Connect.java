package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    Connection con;

    public Connection loading()  {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载数据库驱动");
        } catch (Exception e) {
            System.out.println("加载数据库驱动出现异常");
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/csgl","root","461900");
            System.out.println("成功连接数据库服务器");
        } catch (Exception e) {
            System.out.println("连接数据库服务器出现错误");
            e.printStackTrace();
        } ;
        return con;
    }

    public void  closecon(java.sql.Connection con) throws SQLException {
        if (con!=null){
            con.close();
        }

    }
}
