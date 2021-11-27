package com.example.comp2411project.util;

import java.sql.SQLException;

public interface Table {
    int CUSTOMER = 0;
    int DELIVERMAN = 1;
    int MERCHANT = 2;
    Table pushInfo() throws SQLException;
    Table pullUpdate();
}
