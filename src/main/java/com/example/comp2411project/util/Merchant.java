package com.example.comp2411project.util;

import com.example.comp2411project.AppLog;
import com.example.comp2411project.func.Cache;
import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Merchant implements Table {
    long merchantId;
    String username;
    String password;
    String phoneNO;
    int positionX;
    int positionY;

    public Merchant(long id){
        this.merchantId =id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public Merchant(String username, String password, String phoneNO) {
        this.username = username;
        this.password = password;
        this.phoneNO = phoneNO;
        Random random = new Random(System.currentTimeMillis());
        this.positionX = random.nextInt(100);
        this.positionY = random.nextInt(100);
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
    public Table pushInfo() throws SQLException{
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("MERCHANT", "MERCHANTID", merchantId);
        if(hasValue){
            oracleDB.update("UPDATE MERCHANT "+
                    "SET USERNAME = ?, PASSWORD = ?, MERCHANT = ?, PX = ?, PY = ? "+
                    "WHERE MERCHANTID = ?", username, password, phoneNO, positionX, positionY, merchantId);

        }else{
            merchantId = oracleDB.insert("INSERT INTO MERCHANT(USERNAME, PASSWORD, PHONENO, PX, PY) VALUES(?, ?, ?, ?, ?)", "MERCHANTID", username, password, phoneNO, positionX, positionY);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, PX, PY FROM MERCHANT WHERE MERCHANTID = ?", merchantId)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                positionX = rs.getInt(4);
                positionY = rs.getInt(5);
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
        AppLog.getInstance().log(String.format("Merchant ID = %d", merchantId));
        try(ResultSet rs = oracleDB.query("SELECT GOODSID FROM GOODS WHERE MERCHANTID = ?", merchantId)){
            while(rs.next()){
                long oid = rs.getLong(1);
                AppLog.getInstance().debug(String.format("good id = %d", oid));
                Goods goods;
                if(cache.getOrderHashMap().containsKey(oid)) {
                    AppLog.getInstance().debug("contains");
                    goods = (Goods) cache.getGoodsHashMap().get(oid).pullUpdate();
                }else{
                    goods = (Goods) new Goods(oid).pullUpdate();
                    AppLog.getInstance().debug("Not contains");
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

    public void recieveOrder(Order order) throws SQLException{
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

    public Order updateOrder(Order order) throws NoSuchFieldException, SQLException{
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
        try(ResultSet rs = oracle.query("SELECT PASSWORD, MERCHANTID FROM MERCHANT WHERE USERNAME = ?", username)){
            if(rs.next()){
                String realPassword = rs.getString(1);
                if(password.equals(realPassword)) {
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

    public static ArrayList<Merchant> getMerchantList() throws SQLException{
        OracleDB oracle = OracleDB.getInstance();
        Cache cache = Cache.getInstance();
        ArrayList<Merchant> ret = new ArrayList<>();
        oracle.getConnection();
        ResultSet rs = oracle.query("SELECT MERCHANTID FROM MERCHANT", (Object[]) null);
        while(rs.next()){
            long id = rs.getLong(1);
            Merchant merchant = cache.getMerchant(id);
            ret.add(merchant);
        }
        oracle.closeConnection();
        return ret;
    }

    public Goods addGoods(double price, String name, int count) throws SQLException{
        Goods ret = new Goods(price, name, count);
        ret.setMerchantID(this.merchantId);
        ret.pushInfo();
        return ret;
    }

}
