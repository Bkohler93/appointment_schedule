package com.example.appointment_schedule;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Time;

/**
 * @author Brett Kohler
 * contains constant values to be used throughout the program
 * @author  <a href="https://stackoverflow.com/questions/12517978/java-constant-examples-create-a-java-file-having-only-constants">reference</a>
 */
public final class Constants {

    /**
     * prevents instantiation of constants
     */
    private Constants() {
        // restrict instantiation
    }

    public static final ObservableList<String> months = FXCollections.observableArrayList("January", "February", "March", "April",
            "May", "June", "July", "August", "September", "October", "November", "December");

    private static final String DB_PROTOCOL = "jdbc";
    private static final String DB_VENDOR = ":mysql:";
    private static final String DB_LOCATION = "//localhost/";
    private static final String DATABASE_NAME = "client_schedule";
    public static final String JDBC_URL = DB_PROTOCOL + DB_VENDOR + DB_LOCATION + DATABASE_NAME + "?connectionTimeZone = SERVER"; // LOCAL
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver"; // Driver reference
    public static final String DB_USER_NAME = "sqlUser"; // Username
    public static final String DB_PASSWORD = "Passw0rd!"; // Password

    public static final Time BUSINESS_HOURS_START_EST = Time.valueOf("07:59:59");
    public static final Time BUSINESS_HOURS_END_EST = Time.valueOf("22:00:01");
    public static final String BUSINESS_TIMEZONE = "America/New_York";
}
