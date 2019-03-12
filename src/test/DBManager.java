/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {

    public DBManager() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            String URL = "jdbc:mysql://localhost:3306/test";
            String username = "root";
            String password = "1234";
            connection = DriverManager.getConnection(URL, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private Statement getStatement() {
        Connection connection;
        Statement stmt;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            return stmt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getResultSet(String sql, boolean isQuery) {
        ResultSet rs = null;
        Statement stmt;
        stmt = getStatement();
        try {
            if (isQuery) {
                assert stmt != null;
                rs = stmt.executeQuery(sql);
            } else {
                assert stmt != null;
                stmt.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return rs;
    }
}
