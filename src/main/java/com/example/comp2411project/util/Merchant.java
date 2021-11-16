package com.example.comp2411project.util;

import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Merchant implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    String address;
    double positionX;
    double positionY;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public double getPositionX() {
        return positionX;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }

    @Override
    public void pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("MERCHANT", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE MERCHANT "+
                    "SET USERNAME = ?, PASSWORD = ?, MERCHANT = ?, ADDRESS = ? "+
                    "WHERE ID = ?", username, password, phoneNO, address, id);

        }else{
            id = oracleDB.insert("INSERT INTO MERCHANT(USERNAME, PASSWORD, PHONENO, ADDRESS) VALUES(?, ?, ?, ?)",username, password, phoneNO, address);
        }
        oracleDB.closeConnection();
    }

    @Override
    public void pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ADDRESS FROM MERCHANT WHERE ID = ?", id)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                address = rs.getString(4);
            }
        }catch (SQLException e){
            System.out.println("Query Error: ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
    }

    public HashMap<Long, Order> getOrderList(){
        HashMap<Long, Order> ret = new HashMap<>();
        OracleDB oracleDB = OracleDB.getInstance();
        Cache cache = Cache.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT ID FROM ORDER WHERE MERCHANTID = ?", id)){
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
}
