package com.example.comp2411project.func;

import java.sql.*;
import oracle.jdbc.driver.*;
import oracle.sql.*;

public class OracleDB {
    private static OracleDB instance = new OracleDB("20075563d", "123456");

    public static OracleDB getInstance() {
        return instance;
    }

    public static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final String URL = "jdbc:oracle:thin:@studora.comp.polyu.edu.hk:1521:dbms";

    String username, password;
    private OracleDB(String username, String password){
        this.username = username;
        this.password = password;
    }

    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    public Connection getConnection(){
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, username, password);
        }catch (ClassNotFoundException e){

        } catch (SQLException e){
            System.out.println("Connection Error: ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        try{
            connection.setAutoCommit(true);
        }catch (SQLException e){
            System.out.println("Set auto-commit error!" );
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }

        return connection;
    }

    public void closeConnection(){
        try{
            if(ps != null){
                ps.close();
            }
            if(rs != null){
                rs.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        catch (SQLException e){
            System.out.println("Close Database Failed. ");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public boolean existValue(String table, String attr, Object obj){
        boolean ret = false;
        try{
            ps = connection.prepareStatement("SELECT 1 FROM ? WHERE ? = ?");
            ps.setObject(1, table);
            ps.setObject(2, attr);
            ps.setObject(3, obj);
            rs = ps.executeQuery();
            if(rs.next())
                ret = true;
        }catch (SQLException e){
            System.out.println("Query Database failed.");
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        return ret;
    }
    public long insert(String sql, Object... paramList){
        long ret = -1;
        try{
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if(paramList != null) {
                for (int i = 0; i < paramList.length; ++i) {
                    ps.setObject(i + 1, paramList[i]);
                }
            }
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if(rs.next()){
                ret = rs.getLong(1);
            }
        }catch (SQLException e){
            System.out.println("Error in executing the sql code: " + sql);
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        return ret;
    }

    public void update(String sql, Object... paramList){
        try{
            ps = connection.prepareStatement(sql);
            if(paramList != null) {
                for (int i = 0; i < paramList.length; ++i) {
                    ps.setObject(i+1, paramList[i]);
                }
            }
            ps.executeUpdate();
        }catch (SQLException e){
            System.out.println("Error in executing the sql code: " + sql);
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public ResultSet query(String sql, Object... paramList){
        try{
            ps = connection.prepareStatement(sql);
            if(paramList != null) {
                for (int i = 0; i < paramList.length; ++i) {
                    ps.setObject(i+1, paramList[i]);
                }
                rs = ps.executeQuery();
            }
        }catch (SQLException e){
            System.out.println("Error in executing the sql code: " + sql);
            while(e != null){
                System.out.println("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        return rs;
    }
}
