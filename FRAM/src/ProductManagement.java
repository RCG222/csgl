import Dao.ProductDao;
import utils.Connect;

import javax.swing.*;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductManagement  {
    private DefaultTableModel model;
    private JPanel panel1;
    private JTable table1;
    private JButton 添加Button;
    private JButton 修改Button;
    private JButton 查询Button;
    private  JPopupMenu m_popupMenu;
    String[][] datas = {};
    ProductDao productDao = new ProductDao();//创建连接；
    JMenuItem delMenItem = new JMenuItem();

//===========================================================================================================创建表格表头



    public ProductManagement() {
        go();
//==========================================================================窗口初始化
        JFrame frame = new JFrame("ProductManagement");
        frame.add(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocation(500,100);
        frame.setVisible(true);

//===========================================================================右击删除
        m_popupMenu = new JPopupMenu();
        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText("  删除  ");
        m_popupMenu.add(delMenItem);
//===========================================================================删除监听时间
        delMenItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               System.out.println(1);
                int j = table1.getSelectedRow();
                try {
                    productDao.del(HOME.getHomeCoonnect(),(int)model.getValueAt(j,0));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                go();

            }
        });

//=================================================================================按钮监听

        查询Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                go();
            }
        });



        修改Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int j = table1.getSelectedRow();
                try {
                    productDao.query(HOME.getHomeCoonnect(),(int)model.getValueAt(j,0),(String) model.getValueAt(j,1),(String) model.getValueAt(j,2),(String) model.getValueAt(j,3),(String) model.getValueAt(j,5),Double.parseDouble(model.getValueAt(j,6).toString()),Double.parseDouble(model.getValueAt(j,7).toString()),Integer.parseInt(model.getValueAt(j,8).toString()));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                go();
            }
        });




        添加Button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
                NewProduct newProduct = new NewProduct();

            }
        });
//=============================================================================================监听表事件
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton()==MouseEvent.BUTTON3){
                    int focusedRowIndex = table1.rowAtPoint(e.getPoint());
                    if (focusedRowIndex == -1) {
                        return;
                    }
                    table1.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
                    m_popupMenu.show(table1,e.getX(),e.getY());
                    m_popupMenu.setVisible(true);
                }
            }
        });
    }


//===================================================================================================表结构


    private void createUIComponents() {
        String[] titles = { "货品ID", "货品名称","货品规格","货品条码","名称缩写","计量单位","零售价", "促销价","货品状态"};
        model = new DefaultTableModel(datas, titles);
        table1 = new JTable(model);
    }


//=====================================================================================================刷新页面


    public void  go(){
            try {
                model.setRowCount(0);
                int a=-1;
                int z =1;
                ResultSet resultSet = productDao.query(HOME.getHomeCoonnect(),a,new String(), new String(), new String(), new String(),new Double(1),new Double(1),z);
                int i = 0;
                while (resultSet.next()){
                    model.addRow(datas);
                    table1.setValueAt(resultSet.getInt("product_id"),i,0);
                    table1.setValueAt(resultSet.getString("product_name"),i,1);
                    table1.setValueAt(resultSet.getString("product_specifications"),i,2);
                    table1.setValueAt(resultSet.getString("product_barcode"),i,3);
                    table1.setValueAt(resultSet.getString("product_shortName"),i,4);
                    table1.setValueAt(resultSet.getString("measure_unit"),i,5);
                    table1.setValueAt(resultSet.getDouble("retail_price"),i,6);
                    table1.setValueAt(resultSet.getDouble("bargin_price"),i,7);
                    table1.setValueAt(resultSet.getInt("product_status"),i,8);
                    i++;
                }
                resultSet.close();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

    }



}
