package Dao;

import Product.Chanpin;
import org.junit.jupiter.api.Test;
import utils.Connect;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ProductDaoTest {

    @Test
    void getchanpin() throws SQLException {
            Chanpin chanpin = new Chanpin(new ProductDao().getchanpin("kqs",new Connect().loading()));
            System.out.println(chanpin.getName());
    }
    @Test
    void sell() throws SQLException {
        String[] strings1 = {"1","10001","26","26","0","1","1,1,2,2,1,1"};
        new ProductDao().sell(strings1,new Connect().loading());
    }

}