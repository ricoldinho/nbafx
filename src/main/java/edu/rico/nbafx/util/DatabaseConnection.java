package edu.rico.nbafx.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
                    Properties prop = new Properties();
                    if (input == null) {
                        System.out.println("Sorry, unable to find config.properties");
                        return null;
                    }
                    prop.load(input);
                    
                    connection = DriverManager.getConnection(
                            prop.getProperty("db.url"),
                            prop.getProperty("db.user"),
                            prop.getProperty("db.password"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}