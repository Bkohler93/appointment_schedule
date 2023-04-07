package com.example.appointment_schedule;


import java.sql.Time;

/**
 * @author Brett Kohler
 * @author  <a href="https://stackoverflow.com/questions/12517978/java-constant-examples-create-a-java-file-having-only-constants">reference</a>
 */
public final class Constants {

    /**
     * prevents instantiation of constants
     */
    private Constants() {
        // restrict instantiation
    }

    /**
     * value of Part source label when part is inhouse
     */
    public static final String IN_HOUSE_LABEL = "Machine ID";

    /**
     * value of Part source label when part is outsourced
     */
    public static final String OUTSOURCED_LABEL = "Company Name";

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
