package utils;

import Dao.ProductDao;
import Product.Chanpin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.SQLException;

public class Table_X {
    private JTable table;
    private String xsmx;
    private int liechang;
    private String[] dates;
    private Connection con;
    private String[] titles;
    private ProductDao productDao;
    private DefaultTableModel model;

    public Table_X(JTable table,Connection con) {
        this.con = con;
        this.table = table;

        liechang=0;//记录表的行数

        //初始默认表格模型需要的两个参数
        titles = new String[]{"商品编号", "商品名称", "商品价格", "购买数量"};
        dates = new String[]{};


        productDao=new ProductDao();
        model = new DefaultTableModel();
        model.setColumnIdentifiers(titles);
        table.setModel(getModel());
        table.setRowHeight(40);

    }
    //返回构建表格需要的模型
    public DefaultTableModel getModel() {
        return model;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }


    //添加一行，包括查询，并把查询到的数据放到model中，让
    public void addRow(String xinxi,String shulaing) throws SQLException {
        //判断输入框为空就返回
        if (xinxi.isEmpty()||shulaing.isEmpty()){
            return;
        }
        //新建一个产品对象，产品对象使用数据库返回的结果集做参数，
        Chanpin chanpin = new Chanpin(productDao.getchanpin(xinxi,con));
        dates = new String[]{String.valueOf(chanpin.getId()), chanpin.getName(), String.valueOf(chanpin.getJiage()), String.valueOf(shulaing)};
        model.addRow(dates);
        liechang++;
    }



    public void sell01(int syyid,String ssje,String qtje,String hyid,int fkfs) throws SQLException {
        String[] strings={String.valueOf(syyid), hyid, String.valueOf(getYsje()), ssje, qtje, String.valueOf(fkfs),getXsmx()};
        productDao.sell(strings,con);
        for (int i = 0;model.getRowCount()>0;i++){
            model.removeRow(i);
        }
        liechang=0;
    }


//计算应收金额
    public double getYsje(){
        double ysje = 0;
        for (int i = 0;i<liechang;i++){
            ysje +=  Double.parseDouble((String) table.getValueAt(i,2)) * Double.parseDouble((String) table.getValueAt(i,3));
        }
        return ysje;
    }


//获取销售明细的字符串
    public String getXsmx(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<liechang;i++){
            if (i==0){
                sb.append(table.getValueAt(i,0));
            }else {
                sb.append(",");
                sb.append(table.getValueAt(i,0));
            }
            sb.append(",");
            sb.append(table.getValueAt(i,3));
        }
        xsmx=String.valueOf(sb);
        System.out.println(xsmx);
        return xsmx;
    }
}
