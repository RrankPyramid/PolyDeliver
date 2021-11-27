package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Deliverman implements Table {
    long delivermanId;
    String username;
    String password;
    String phoneNO;
    Long orderNO;
    int px;
    int py;

    public Long getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(Long orderNO) {
        this.orderNO = orderNO;
    }

    public Deliverman(long id) {
        delivermanId = id;
        orderNO = null;
    }

    public Deliverman(String username, String password, String phoneNO) {
        this.username = username;
        this.password = password;
        this.phoneNO = phoneNO;
        this.orderNO = null;
        Random random = new Random(System.currentTimeMillis());
        this.px = random.nextInt(100);
        this.py = random.nextInt(100);
    }

    @Override
    public Table pushInfo() throws SQLException{
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("DELIVERMAN", "ID", delivermanId);
        if(hasValue){
            oracleDB.update("UPDATE DELIVERMAN "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, ORDERNO = ?, PX = ?, PY = ? "+
                    "WHERE ID = ?", username, password, phoneNO, orderNO, px, py, delivermanId);

        }else{
            delivermanId = oracleDB.insert("INSERT INTO DELIVERMAN(USERNAME, PASSWORD, PHONENO, ORDERNO, PX, PY) VALUES(?, ?, ?, ?, ?, ?)",username, password, phoneNO, orderNO, px, py);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ORDERNO, PX, PY FROM DELIVERMAN WHERE ID = ?", delivermanId)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                orderNO = rs.getLong(4);
                if(rs.wasNull())
                    setOrderNO(null);
                px = rs.getInt(5);
                py = rs.getInt(6);
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

    Customer getNowCustomer() throws UnsupportedOperationException{
        if(orderNO == null)
            throw new UnsupportedOperationException();
        OracleDB oracle = OracleDB.getInstance();
        Cache cache = Cache.getInstance();
        Customer customer = null;
        oracle.getConnection();
        try{
            ResultSet rs = oracle.query("SELECT CUSTOMERID FROM ORDER " +
                    "WHERE ORDERID = ?", orderNO);
            if(rs.next()){
                long cid = rs.getLong(1);
                customer = cache.getCustomer(cid);
            }else throw new UnsupportedOperationException();
        }catch (SQLException e){
            AppLog.getInstance().log("Query Error: ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
            throw new UnsupportedOperationException();
        }
        oracle.closeConnection();
        return customer;
    }

    public static long checkUsernameAndPassword(String username, String password) throws IllegalArgumentException{
        OracleDB oracle = OracleDB.getInstance();
        oracle.getConnection();
        long ret = 0;
        boolean hasValue = oracle.existValue("DELIVERMAN", "USERNAME", username);
        if(!hasValue)
            throw new IllegalArgumentException();
        try(ResultSet rs = oracle.query("SELECT PASSWORD, CUSTOMERID FROM DELIVERMAN WHERE USERNAME = ?", username)){
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

    public long getDelivermanId() {
        return delivermanId;
    }

    public void setDelivermanId(long delivermanId) {
        this.delivermanId = delivermanId;
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

    public int getPx() {
        return px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public int getPy() {
        return py;
    }

    public void setPy(int py) {
        this.py = py;
    }
}
