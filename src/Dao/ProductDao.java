package Dao;

import utils.Connect;

import java.sql.*;

public class ProductDao {
    public ResultSet query(Connection con,int a,String b,String c,String d,String f,Double g,Double h,int i) throws SQLException {
            ResultSet r = null;
            CallableStatement  cs = con.prepareCall("call product_modify(?,?,?,?,?,?,?,?)");
            cs.setInt(1,a);
            cs.setString(2,b);
            cs.setString(3,c);
            cs.setString(4,d);
//            cs.setString(5,e);
            cs.setString(5,f);
            cs.setDouble(6,g);
            cs.setDouble(7,h);
            cs.setInt(8,i);

            r = cs.executeQuery();
            return r;

    }

    public void del(Connection con,int a) throws SQLException {
            CallableStatement  cs = con.prepareCall("call dele(?)");
            cs.setInt(1,a);
            cs.executeQuery();
    }
    public ResultSet getchanpin(String s,Connection connection) throws SQLException {
        ResultSet res=null;
        String sql="select product_id,product_name,retail_price from producttable where ";
        if (s.matches("\\d+")){
            sql=sql+"product_id = "+s;
        }else if (s.matches("[a-zA-Z]+")){
            sql=sql+" product_shortName = "+'"'+s+'"';
        } else{
            sql=sql+" product_name = "+'"'+s+'"';
        }
//        System.out.println(sql);
        PreparedStatement statement = connection.prepareStatement(sql);
        res=statement.executeQuery();
        res.next();
        return res;
    }


    public void sell(String[] strings,Connection con) throws SQLException {
        CallableStatement  cs = con.prepareCall( "call SYJLCP7(?,?,?,?,?,?,?)");
        cs.setInt(1,Integer.parseInt(strings[0]));
        cs.setInt(2,Integer.parseInt(strings[1]));
        cs.setDouble(3,Double.parseDouble(strings[2]));
        cs.setDouble(4,Double.parseDouble(strings[3]));
        cs.setDouble(5,Double.parseDouble(strings[4]));
        cs.setInt(6,Integer.parseInt(strings[5]));
        cs.setString(7,strings[6]);
        cs.executeQuery();
    }


}
