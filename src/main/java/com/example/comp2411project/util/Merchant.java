package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Merchant implements Table {
    long merchantId;
    String username;
    String password;
    String phoneNO;
    String address;
    int positionX;
    int positionY;

    public Merchant(long id){
        this.merchantId =id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
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

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    @Override
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("MERCHANT", "ID", merchantId);
        if(hasValue){
            oracleDB.update("UPDATE MERCHANT "+
                    "SET USERNAME = ?, PASSWORD = ?, MERCHANT = ?, ADDRESS = ?, PX = ?, PY = ? "+
                    "WHERE ID = ?", username, password, phoneNO, address, positionX, positionY, merchantId);

        }else{
            merchantId = oracleDB.insert("INSERT INTO MERCHANT(USERNAME, PASSWORD, PHONENO, ADDRESS, PX, PY) VALUES(?, ?, ?, ?, ?, ?)",username, password, phoneNO, address, positionX, positionY);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ADDRESS, PX, PY FROM MERCHANT WHERE ID = ?", merchantId)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                address = rs.getString(4);
                positionX = rs.getInt(5);
                positionY = rs.getInt(6);
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
        try(ResultSet rs = oracleDB.query("SELECT ID FROM ORDERS WHERE MERCHANTID = ?", merchantId)){
            while(rs.next()){
                long oid = rs.getLong(1);
                Order order;
                if(cache.getOrderHashMap().containsKey(oid)) {
                    order = (Order) cache.getOrderHashMap().get(oid).pullUpdate();
                }else{
                    order = (Order) new Order(oid).pullUpdate();
                    cache.getOrderHashMap().put(oid, order);
                }
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

    public HashMap<Long, Goods> getGoodList(){
        HashMap<Long, Goods> ret = new HashMap<>();
        OracleDB oracleDB = OracleDB.getInstance();
        Cache cache = Cache.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT GOODSID FROM GOODS WHERE MERCHANTID = ?", merchantId)){
            while(rs.next()){
                long oid = rs.getLong(1);
                Goods goods;
                if(cache.getOrderHashMap().containsKey(oid)) {
                    goods = (Goods) cache.getGoodsHashMap().get(oid).pullUpdate();
                }else{
                    goods = (Goods) new Goods(oid).pullUpdate();
                    cache.getGoodsHashMap().put(oid, goods);
                }
                ret.put(oid, goods);
            }
        }catch (SQLException e){
            AppLog.getInstance().log("Search the goods list failed. ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        oracleDB.closeConnection();
        return ret;
    }

    public void recieveOrder(Order order){
        OracleDB oracle = OracleDB.getInstance();
        order.setStatus(order.getStatus() + 1);
        order.pushInfo();
    }


    public Deliverman getNearestDeliverman() throws SQLException{
        Cache cache = Cache.getInstance();
        OracleDB oracle = OracleDB.getInstance();
        Deliverman deliverman = null;
        oracle.getConnection();
        ResultSet rs = oracle.query("SELECT NEAREST_DELIVER_ID(?, ?) FROM DUAL", positionX, positionY);
        if(rs.next()){
            long idd = rs.getLong(1);
            deliverman = cache.getDeliverman(idd);
        }
        oracle.closeConnection();
        return deliverman;
    }

    public Order updateOrder(Order order) throws NoSuchFieldException{
        order.setStatus(order.getStatus() + 1);
        order.pushInfo();
        Deliverman deliverman = null;
        try{
            deliverman = getNearestDeliverman();
            if(deliverman == null)
                throw new NoSuchFieldException();
        }
        catch (SQLException e){
            AppLog.getInstance().log("SQL Statement Error");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
            throw new NoSuchFieldException();
        }
        deliverman.setOrderNO(order.getOrderId());
        deliverman.pushInfo();
        return order;
    }

    public static long checkUsernameAndPassword(String username, String password) throws IllegalArgumentException{
        OracleDB oracle = OracleDB.getInstance();
        oracle.getConnection();
        long ret = 0;
        boolean hasValue = oracle.existValue("MERCHANT", "USERNAME", username);
        if(!hasValue)
            throw new IllegalArgumentException();
        try(ResultSet rs = oracle.query("SELECT PASSWORD, CUSTOMERID FROM MERCHANT WHERE USERNAME = ?", username)){
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
}
