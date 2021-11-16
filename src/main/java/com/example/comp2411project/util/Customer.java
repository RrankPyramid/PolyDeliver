package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;

import java.sql.*;

public class Customer implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    String address;

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
    public void pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("CUSTOMER", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE CUSTOMER "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, ADDRESS = ? "+
                    "WHERE ID = ?", username, password, phoneNO, address, id);

        }else{
            id = oracleDB.insert("INSERT INTO CUSTOMER(USERNAME, PASSWORD, PHONENO, ADDRESS) VALUES(?, ?, ?, ?)",username, password, phoneNO, address);
        }
        oracleDB.closeConnection();
    }

    @Override
    public void pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ADDRESS FROM CUSTOMER WHERE ID = ?", id)){
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
}
