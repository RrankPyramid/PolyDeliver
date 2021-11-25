package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Goods implements Table {
    long goodId;
    long merchantID;
    double price;
    String name;
    int counts;

    public Goods(long id) {
        this.goodId = id;
    }

    public long getGoodId() {
        return goodId;
    }

    public void setGoodId(long goodId) {
        this.goodId = goodId;
    }

    public long getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(long merchantID) {
        this.merchantID = merchantID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

    @Override
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("GOODS", "GOODSID", goodId);
        if(hasValue){
            oracleDB.update("UPDATE GOODS "+
                    "SET MERCHANTID = ?, PRICE = ?, NAME = ?, COUNTS = ? "+
                    "WHERE GOODSID = ?", merchantID, price, name, counts, goodId);

        }else{
            goodId = oracleDB.insert("INSERT INTO GOODS(MERCHANTID, PRICE, NAME, COUNTS) VALUES(?, ?, ?, ?)",merchantID, price, name, counts);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT MERCHANTID, PRICE, NAME, COUNTS FROM GOODS WHERE GOODSID = ?", goodId)){
            if(rs.next()){
                merchantID = rs.getInt(1);
                price = rs.getDouble(2);
                name = rs.getString(3);
                counts = rs.getInt(4);
            }
        }catch (SQLException e){
            AppLog.getInstance().log("pull Error: ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return this;
    }

}
