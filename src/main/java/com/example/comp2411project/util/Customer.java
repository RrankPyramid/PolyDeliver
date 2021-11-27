package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Customer implements Table {
    long customerId;
    String username;
    String password;
    String phoneNO;
    int px;
    int py;

    public Customer(long id) {
        customerId = id;
    }

    public Customer(String username, String password, String phoneNO) {
        this.username = username;
        this.password = password;
        this.phoneNO = phoneNO;
        Random random = new Random(System.currentTimeMillis());
        px = random.nextInt(100);
        py = random.nextInt(100);
    }


    Order makeOrder(Goods[] goods, Merchant merchant){
        return null;
    }

    @Override
    public Table pushInfo() throws SQLException{
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("CUSTOMER", "ID", customerId);
        if(hasValue){
            oracleDB.update("UPDATE CUSTOMER "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, PX = ?, PY = ? "+
                    "WHERE ID = ?", username, password, phoneNO, px, py, customerId);

        }else{
            customerId = oracleDB.insert("INSERT INTO CUSTOMER(USERNAME, PASSWORD, PHONENO, PX, PY) VALUES(?, ?, ?, ?, ?)",username, password, phoneNO, px, py);
        }
        oracleDB.closeConnection();
        return this;
    }

    // select is null

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, PX, PY FROM CUSTOMER WHERE ID = ?", customerId)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                px = rs.getInt(4);
                py = rs.getInt(5);
            }
        }catch (SQLException e){
            AppLog.getInstance().log("Query Error: ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return this;
    }

    public HashMap<Long, Order> getOrderList(){
        HashMap<Long, Order> ret = new HashMap<>();
        OracleDB oracleDB = OracleDB.getInstance();
        Cache cache = Cache.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT ID FROM ORDER WHERE CUSTOMERID = ?", customerId)){
            while(rs.next()){
                long oid = rs.getLong(1);
                Order order;
                if(cache.getOrderHashMap().containsKey(oid)) {
                    order = cache.getOrderHashMap().get(oid);
                }else{
                    order = new Order(oid);
                    cache.getOrderHashMap().put(oid, order);
                }
                order.pullUpdate();
                ret.put(oid, order);
            }
        }catch (SQLException e){
            AppLog.getInstance().log("Search the order list failed. ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return ret;
    }

    public Order makeOrder(long merchantID, Set<Long> goodIDs) throws SQLException{
        Cache cache = Cache.getInstance();
        Merchant merchant = cache.getMerchant(merchantID);
        double price = goodIDs.stream().mapToDouble(k -> cache.getGoods(k).getPrice()).sum();
        Order order = (Order) new Order(merchant.getMerchantId(), this.getCustomerId(), goodIDs, price, 1).pushInfo();
        return order;
    }

    public void confirmRecieve(Order order) throws SQLException{
        order.setStatus(order.getStatus() + 1);
        order.pushInfo();
    }

    public static long checkUsernameAndPassword(String username, String password) throws IllegalArgumentException{
        OracleDB oracle = OracleDB.getInstance();
        oracle.getConnection();
        long ret = 0;
        boolean hasValue = oracle.existValue("CUSTOMER", "USERNAME", username);
        if(!hasValue)
            throw new IllegalArgumentException();
        try(ResultSet rs = oracle.query("SELECT PASSWORD, CUSTOMERID FROM CUSTOMER WHERE USERNAME = ?", username)){
            if(rs.next()){
                String realPassword = rs.getString(1);
                if(password.equals(realPassword)) {
                    if(!rs.next())
                        throw new SQLException();
                    ret = rs.getLong(2);
                }
            }
            else throw new IllegalArgumentException();
        }catch (SQLException e){
            AppLog.getInstance().log("Find Password Failed.");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
            throw new IllegalArgumentException();
        }
        oracle.closeConnection();
        return ret;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }
}
