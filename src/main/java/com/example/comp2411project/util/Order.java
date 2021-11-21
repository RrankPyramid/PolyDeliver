package com.example.comp2411project.util;

import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Order implements Table{

    long id;
    long deliverManID;
    long merchantID;
    long customerID;
    Set<Long> goodIDs;
    double price;
    int status;
    /*
    Status -1: Order rejected
    Status 0: Error;
    Status 1: Order sent
    Status 2: Order recieved
    Status 3: Waiting for delivering
    Status 4: Shipping
    Status 5: Done
     */

    public Order(long id) {
        this.id = id;
    }

    public Order(long deliverManID, long merchantID, long customerID, Set<Long> goodIDs, double price, int status) {
        this.id = -1;
        this.deliverManID = deliverManID;
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
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("ORDER", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE ORDER "+
                    "SET DELIVERMANID = ?, MERCHANTID = ?, CUSTOMERID = ?, PRICE = ?, STATUS = ? " +
                    "WHERE ID = ?", deliverManID, merchantID, customerID, price, status, id);
        }else{
            id = oracleDB.insert("INSERT INTO ORDER(DELIVERMANID, MERCHANTID, CUSTOMERID, PRICE, STATUS) VALUES(?, ?, ?, ?, ?)", deliverManID, merchantID, customerID, price, status);
            for(long goodID : goodIDs){
                oracleDB.insert("INSERT INTO ORDER2GOODS(ORDERID, GOODID) VALUES(?, ?)", id, goodID);
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
            ResultSet rs = oracleDB.query("SELECT DELIVERMANID, MERCHANTID, CUSTOMERID, PRICE, STATUS FROM ORDER WHERE ID = ?", id);
            if(rs.next()){
                deliverManID = rs.getLong(1);
                merchantID = rs.getLong(2);
                customerID = rs.getLong(3);
                price = rs.getDouble(4);
                status = rs.getInt(5);
            }
            if(goodIDs == null){
                goodIDs = new TreeSet<>();
                rs = oracleDB.query("SELECT GOODID FROM ORDER2GOODS WHERE ORDERID = ?", id);
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
