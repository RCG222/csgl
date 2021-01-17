package Product;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Chanpin {
    private int id;
    private String name;
    private double jiage;
    public Chanpin(int id, String name, double jiage) {
        this.id = id;
        this.name = name;
        this.jiage = jiage;
    }
    public Chanpin(ResultSet resultSet) throws SQLException {
        this.id=resultSet.getInt("product_id");
        this.name=resultSet.getString("product_name");
        this.jiage=resultSet.getDouble("retail_price");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getJiage() {
        return jiage;
    }

    public void setJiage(double jiage) {
        this.jiage = jiage;
    }
}
