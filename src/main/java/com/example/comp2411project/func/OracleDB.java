package com.example.comp2411project.func;

import java.sql.*;

import com.example.comp2411project.AppLog;
import oracle.jdbc.OraclePreparedStatement;
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
            AppLog.getInstance().error("Connection Error: ");
            while(e != null){
                AppLog.getInstance().error("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        try{
            connection.setAutoCommit(true);
            AppLog.getInstance().log("Auto Commit Set.");
        }catch (SQLException e){
            AppLog.getInstance().error("Set auto-commit error!" );
            while(e != null){
                AppLog.getInstance().error("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        AppLog.getInstance().log("Oracle Connection Success");
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
            AppLog.getInstance().log("Close Database Failed. ");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
    }

    public boolean existValue(String table, String attr, Object obj){
        boolean ret = false;
        if(obj == null){
            AppLog.getInstance().error("obj is null!");
            return false;
        }
        try{
            ps = connection.prepareStatement(String.format("SELECT 1 FROM %s WHERE %s = ?", table, attr));
            ps.setObject(1, obj);
            rs = ps.executeQuery();
            if(rs.next())
                ret = true;
            else
                AppLog.getInstance().log("rs has not return value! ");
        }catch (SQLException e){
            AppLog.getInstance().log("Query Database failed.");
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        return ret;
    }
    public Long insert(String sql, Object... paramList) throws SQLException{
        Long ret = null;
        try{
            String[] id_name = {(String) paramList[0]};

            ps = connection.prepareStatement(sql, id_name);
            for (int i = 1; i < paramList.length; ++i) {
                if(paramList[i] != null)
                    ps.setObject(i, paramList[i]);
                else{
//                    AppLog.getInstance().log("before setting null");
                    ps.setNull(i, Types.INTEGER);
//                    AppLog.getInstance().log("after setting null");
                }

            }
//            AppLog.getInstance().log("Before insert execute");
            ps.executeUpdate();
//            AppLog.getInstance().log("After execute");

            rs = ps.getGeneratedKeys();
            if(rs.next()){
                ret = rs.getLong(1);
            }
        }catch (SQLException e){
            SQLException e2 = e;
            AppLog.getInstance().error("Error in executing the sql code: " + sql);
            while(e2 != null){
                AppLog.getInstance().error("message: " + e2.getMessage());
                e2 = e2.getNextException();
            }
            throw e;
        }
        return ret;
    }

    public void update(String sql, Object... paramList){
        try{
            ps = connection.prepareStatement(sql);
            if(paramList != null) {
                for (int i = 0; i < paramList.length; ++i) {
                    if(paramList[i] != null)
                        ps.setObject(i+1, paramList[i]);
                    else
                        ps.setObject(i+1, Types.NULL);
                }
            }
            ps.executeUpdate();
        }catch (SQLException e){
            AppLog.getInstance().log("Error in executing the sql code: " + sql);
            while(e != null){
                AppLog.getInstance().log("message: " + e.getMessage());
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
            }
            rs = ps.executeQuery();
        }catch (SQLException e){
            AppLog.getInstance().error("Error in executing the sql code: " + sql);
            while(e != null){
                AppLog.getInstance().error("message: " + e.getMessage());
                e = e.getNextException();
            }
        }
        return rs;
    }
}
