package com.restaurant.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.restaurant.exceptions.DatabaseException;

public class DatabaseManager {

    private static final String URL =
        "jdbc:sqlserver://localhost:1433;" +
        "databaseName=RestaurantDB;user=Practica_Marin;password=practica123;" +
        "encrypt=false;trustServerCertificate=true;";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(URL);
            return connection;
        } catch (SQLException e) {
            throw new DatabaseException("Conexiune esuata", e);
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

    public static String testConnection() {
        try { getConnection(); return "Conectat la RestaurantDB"; }
        catch (DatabaseException e) { return "Eroare: " + e.getCause().getMessage(); }
    }
}