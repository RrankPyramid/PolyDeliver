package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Order implements Table{

    long orderId;
    long merchantID;
    long customerID;
    Set<Long> goodIDs;
    double price;
    int status;
    Date createTime;
    Date modifyTime;
    /*
    Status -1: Order rejected
    Status 0: Error;
    Status 1: Order sent
    Status 2: Order recieved
    Status 3: Shipping
    Status 4: Done
     */

    public Order(long id) {
        this.orderId = id;
    }

    public Order(long merchantID, long customerID, Set<Long> goodIDs, double price, int status) {
        this.orderId = -1;
        this.merchantID = merchantID;
        this.customerID = customerID;
        this.goodIDs = goodIDs;
        this.price = price;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("ORDERS", "ORDERID", orderId);
        if(hasValue){
            oracleDB.update("UPDATE ORDERS "+
                    "SET MERCHANTID = ?, CUSTOMERID = ?, PRICE = ?, STATUS = ? " +
                    "WHERE ORDERID = ?", merchantID, customerID, price, status, orderId);
        }else{
            orderId = oracleDB.insert("INSERT INTO ORDERS(MERCHANTID, CUSTOMERID, PRICE, STATUS) VALUES(?, ?, ?, ?, ?)", merchantID, customerID, price, status);
            for(long goodID : goodIDs){
                oracleDB.insert("INSERT INTO ORDERS2GOODS(ORDERID, GOODSID) VALUES(?, ?)", orderId, goodID);
            }
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try{
            ResultSet rs = oracleDB.query("SELECT MERCHANTID, CUSTOMERID, PRICE, STATUS FROM ORDERS WHERE ORDERID = ?", orderId);
            if(rs.next()){
                merchantID = rs.getLong(1);
                customerID = rs.getLong(2);
                price = rs.getDouble(3);
                status = rs.getInt(4);
            }
            if(goodIDs == null){
                goodIDs = new TreeSet<>();
                rs = oracleDB.query("SELECT GOODSID FROM ORDERS2GOODS WHERE ORDERID = ?", orderId);
                while (rs.next()){
                    long goodID = rs.getLong(1);
                    goodIDs.add(goodID);
                }
            }
            rs = oracleDB.query("SELECT CREATE_TIME, MODIFY_TIME FROM ORDERS " +
                    "WHERE ORDERID = ?", orderId);
            if(rs.next()){
                createTime = rs.getTimestamp(1);
                modifyTime = rs.getTimestamp(2);
            }
        }catch (SQLException e){
            AppLog.getInstance().log("Pull Error: ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return this;
    }

    public HashMap<Long, Goods> getGoodsList(){
        if(getGoodIDs() == null){
            pullUpdate();
        }
        HashMap<Long, Goods> ret = new HashMap<>();
        for(long gid : getGoodIDs()){
            if(Cache.getInstance().getGoodsHashMap().containsKey(gid)){
                Goods goods = Cache.getInstance().getGoodsHashMap().get(gid);
                goods.pullUpdate();
                ret.put(gid, goods);
            }
            else {
                Goods goods = new Goods(gid);
                goods.pullUpdate();
                ret.put(gid, goods);
            }
        }
        return ret;
    }

}
