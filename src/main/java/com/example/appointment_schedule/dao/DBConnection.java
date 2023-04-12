package com.example.appointment_schedule.dao;

import com.example.appointment_schedule.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Class to maintain DB connection. Opens and closes the connection, and exposes the connection for queries
 * @author Brett Kohler
 */
public class DBConnection {
    public static Connection connection;  // Connection Interface

    /**
     * opens database connection
     */
    public static void openConnection()
    {
        try {
            Class.forName(Constants.DB_DRIVER); // Locate Driver
            Properties props = new Properties();
            props.setProperty("user", Constants.DB_USER_NAME);
            props.setProperty("password", Constants.DB_PASSWORD);
            props.setProperty("connectionTimeZone", "UTC");
            connection = DriverManager.getConnection(Constants.JDBC_URL, props); // Reference Connection object

            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

    /**
     * closes connection
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
