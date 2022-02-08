package com.example.comp2411project;

import com.example.comp2411project.func.OracleDB;
import javafx.application.Application;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        OracleDB oracle = OracleDB.getInstance();
        oracle.getConnection();
//        ResultSet rs = oracle.query("SELECT * FROM GOODS WHERE MERCHANTID = 2", (Object[]) null);
//        if(rs.next()){
//            System.out.println("23333");
//        }
//        else{
//            System.out.println("????");
//        }
        oracle.closeConnection();
        Application.launch(PolyUBazApplication.class);
    }

}

