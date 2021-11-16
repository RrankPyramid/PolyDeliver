package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Merchant implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    String address;

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
}
