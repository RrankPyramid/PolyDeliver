package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Deliverman implements Table {
    long id;
    String username;
    String password;
    String phoneNO;
    int orderNO;

    @Override
    public void pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("DELIVERMAN", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE DELIVERMAN "+
                    "SET USERNAME = ?, PASSWORD = ?, PHONENO = ?, ORDERNO = ? "+
                    "WHERE ID = ?", username, password, phoneNO, orderNO, id);

        }else{
            id = oracleDB.insert("INSERT INTO DELIVERMAN(USERNAME, PASSWORD, PHONENO, ORDERNO) VALUES(?, ?, ?, ?)",username, password, phoneNO, orderNO);
        }
        oracleDB.closeConnection();
    }

    @Override
    public void pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ORDERNO FROM DELIVERMAN WHERE ID = ?", id)){
            if(rs.next()){
                username = rs.getString(1);
                password = rs.getString(2);
                phoneNO = rs.getString(3);
                orderNO = rs.getInt(4);
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
