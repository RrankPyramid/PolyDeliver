package com.example.comp2411project.util;

import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Customer implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    String address;
    double px;
    double py;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    Order makeOrder(Goods[] goods, Merchant merchant){
        return null;
    }

    @Override
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("CUSTOMER", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE CUSTOMER "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, ADDRESS = ?, PX = ?, PY = ? "+
                    "WHERE ID = ?", username, password, phoneNO, address, px, py, id);

        }else{
            id = oracleDB.insert("INSERT INTO CUSTOMER(USERNAME, PASSWORD, PHONENO, ADDRESS, PX, PY, id) VALUES(?, ?, ?, ?)",username, password, phoneNO, px, py, address);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ADDRESS, PX, PY FROM CUSTOMER WHERE ID = ?", id)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                address = rs.getString(4);
                px = rs.getDouble(5);
                py = rs.getDouble(6);
            }
        }catch (SQLException e){
            System.out.println("Query Error: ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
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
        try(ResultSet rs = oracleDB.query("SELECT ID FROM ORDER WHERE CUSTOMERID = ?", id)){
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
            System.out.println("Search the order list failed. ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return ret;
    }

    public Order makeOrder(long merchantID, Set<Long> goodIDs){
        Cache cache = Cache.getInstance();
        Merchant merchant = cache.getMerchant(merchantID);
        Deliverman deliverman = merchant.getNearestDeliverman();
        double price = goodIDs.stream().mapToDouble(k -> cache.getGoods(k).getPrice()).sum();
        Order order = (Order) new Order(deliverman.getId(), merchant.getId(), this.getId(), goodIDs, price, 1).pushInfo();
        return order;
    }
}
