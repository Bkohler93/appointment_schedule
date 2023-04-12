package com.example.appointment_schedule.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.appointment_schedule.dao.DBConnection.connection;

/**
 * Handles all query execution and exposes a getter for a ResultSet from the executed query
 * @author Brett Kohler
 */
public class Query {
    private static ResultSet result;

    /**
     * uses a string formatted as a SQL statement and executes the query, saving the result to the ResultSet
     * @param q query to execute
     * @throws SQLException thrown by invalid SQL syntax
     */
    public static void makeQuery(String q) throws SQLException {
        Statement statement = connection.createStatement();

        if (q.toLowerCase().startsWith("select")) {
            result = statement.executeQuery(q);
        }
        if (q.toLowerCase().startsWith("delete") || q.toLowerCase().startsWith("insert") || q.toLowerCase().startsWith("update")) {
            statement.executeUpdate(q);
        }
    }

    /**
     * gets the current ResultSet to retrieve data from
     * @return the ResultSet
     */
    public static ResultSet getResult() { return result; }
}
