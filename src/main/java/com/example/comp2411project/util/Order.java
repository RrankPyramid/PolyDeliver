package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class Order implements Table{

    long id;
    long deliverManID;
    long merchantID;
    long customerID;
    Set<Long> goodIDs;
    double price;

    public Order(long id, long deliverManID, long merchantID, long customerID, Set<Long> goodIDs, double price) {
        this.id = id;
        this.deliverManID = deliverManID;
        this.merchantID = merchantID;
        this.customerID = customerID;
        this.goodIDs = goodIDs;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDeliverManID() {
        return deliverManID;
    }

    public void setDeliverManID(long deliverManID) {
        this.deliverManID = deliverManID;
    }

    public long getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(long merchantID) {
        this.merchantID = merchantID;
    }

    public long getCustomerID() {
        return customerID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }

    public Set<Long> getGoodIDs() {
        return goodIDs;
    }

    public void setGoodIDs(Set<Long> goodIDs) {
        this.goodIDs = goodIDs;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public void pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("ORDER", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE ORDER "+
                    "SET DELIVERMANID = ?, MERCHANTID = ?, CUSTOMERID = ?, PRICE = ? " +
                    "WHERE ID = ?", deliverManID, merchantID, customerID, price, id);

        }else{
            id = oracleDB.insert("INSERT INTO ORDER(DELIVERMANID, MERCHANTID, CUSTOMERID, PRICE) VALUES(?, ?, ?, ?)", deliverManID, merchantID, customerID, price);
            for(long goodID : goodIDs){
                oracleDB.insert("INSERT INTO ORDER2GOODS(ORDERID, GOODID) VALUES(?, ?)", id, goodID);
            }
        }
        oracleDB.closeConnection();
    }

    @Override
    public void pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try{
            ResultSet rs = oracleDB.query("SELECT DELIVERMANID, MERCHANTID, CUSTOMERID, PRICE FROM ORDER WHERE ID = ?", id);
            if(rs.next()){
                deliverManID = rs.getLong(1);
                merchantID = rs.getLong(2);
                customerID = rs.getLong(3);
                price = rs.getDouble(4);
            }
            if(goodIDs == null){
                rs = oracleDB.query("SELECT DISTINCT GOODID FROM ORDER2GOODS WHERE ORDERID = ?", id);
                while (rs.next()){
                    long goodID = rs.getLong(1);
                    goodIDs.add(goodID);
                }
            }
        }catch (SQLException e){
            System.out.println("Pull Error: ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
    }
}