package com.example.appointment_schedule.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.example.appointment_schedule.dao.DBConnection.connection;

public class Query {
    private static String query;
    private static Statement statement;
    private static ResultSet result;
    public static void makeQuery(String q) throws SQLException {
        query = q;
        try {
            statement = connection.createStatement();

            if (query.toLowerCase().startsWith("select")) {
                result = statement.executeQuery(query);
            }
            if (query.toLowerCase().startsWith("delete") || query.toLowerCase().startsWith("insert") || query.toLowerCase().startsWith("update")) {
                statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            System.out.println(e);
            throw e;
        }
    }

    public static ResultSet getResult() { return result; }
}
