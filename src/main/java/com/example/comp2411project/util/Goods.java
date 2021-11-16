package com.example.comp2411project.util;

import com.example.comp2411project.func.OracleDB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Goods implements Table {
    long id;
    long merchantID;
    double price;
    String name;
    @Override
    public void pushInfo(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        boolean hasValue = oracleDB.existValue("GOODS", "ID", id);
        if(hasValue){
            oracleDB.update("UPDATE GOODS "+
                    "SET MERCHANTID = ?, PRICE = ?, NAME = ? "+
                    "WHERE ID = ?", merchantID, price, name, id);

        }else{
            id = oracleDB.insert("INSERT INTO GOODS(MERCHANTID, PRICE, NAME) VALUES(?, ?, ?)",merchantID, price, name);
        }
        oracleDB.closeConnection();
    }

    @Override
    public void pullUpdate(){
        OracleDB oracleDB = OracleDB.getInstance();
        oracleDB.getConnection();
        try(ResultSet rs = oracleDB.query("SELECT USERNAME, PASSWORD, PHONENO, ADDRESS FROM GOODS WHERE ID = ?", id)){
            if(rs.next()){
                merchantID = rs.getInt(1);
                price = rs.getDouble(2);
                name = rs.getString(3);
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
