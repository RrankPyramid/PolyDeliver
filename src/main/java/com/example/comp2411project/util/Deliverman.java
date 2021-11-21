package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;
import javafx.util.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Deliverman implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    int orderNO;
    double px;
    double py;

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

    public int getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(int orderNO) {
        this.orderNO = orderNO;
    }

    public double getPx() {
        return px;
    }

    public void setPx(double px) {
        this.px = px;
    }

    public double getPy() {
        return py;
    }

    public void setPy(double py) {
        this.py = py;
    }

    @Override
    public Table pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("DELIVERMAN", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE DELIVERMAN "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, ORDERNO = ?, X = ?, Y = ? "+
                    "WHERE ID = ?", username, password, phoneNO, orderNO, px, py, id);

        }else{
            id = oracleDB.insert("INSERT INTO DELIVERMAN(USERNAME, PASSWORD, PHONENO, ORDERNO, X, Y) VALUES(?, ?, ?, ?, ?, ?)",username, password, phoneNO, orderNO, px, py);
        }
        oracleDB.closeConnection();
        return this;
    }

    @Override
    public Table pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ORDERNO, X, Y FROM DELIVERMAN WHERE ID = ?", id)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                orderNO = rs.getInt(4);
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


}
