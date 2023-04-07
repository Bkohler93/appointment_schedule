package com.example.appointment_schedule.util;


import com.example.appointment_schedule.Constants;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    public static void main(String[] args) {

        /// testing formValueToUTCTimestamp
        String date = "2023/04/06";
        String time = "15:30";
        System.out.println("Resultant Timestamp: " +formValueToUTCTimestamp(date, time));


        /// testing areDatesBetween()
        Timestamp proposedValidStart = Timestamp.valueOf("2023-04-06 09:00:00");
        Timestamp proposedValidEnd = Timestamp.valueOf("2023-04-06 10:00:00");
        Timestamp proposedInvalidStart = Timestamp.valueOf("2023-04-06 16:00:00");
        Timestamp proposedInvalidEnd = Timestamp.valueOf("2023-04-06 18:00:00");
        Time businessStart = Time.valueOf("13:00:00");
        Time businessEnd = Time.valueOf("03:00:00");

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        Time validStartTimeUTC = Time.valueOf(timeFormat.format(proposedValidStart.getTime()));
        Time validEndTimeUTC =Time.valueOf(timeFormat.format(proposedValidEnd.getTime()));
        Time invalidStartTimeUTC = Time.valueOf(timeFormat.format(proposedInvalidStart.getTime()));
        Time invalidEndTimeUTC = Time.valueOf(timeFormat.format(proposedInvalidEnd.getTime()));

        if (areDatesBetween(validStartTimeUTC, validEndTimeUTC, businessStart, businessEnd)) {
            System.out.println("PASS: Valid times accepted by areDatesBetween()");
        } else {
            System.out.println("FAIL: Valid times not accepted by areDatesBetween()");
        }

        if (!areDatesBetween(invalidStartTimeUTC, invalidEndTimeUTC, businessStart, businessEnd)) {
            System.out.println("PASS: Invalid times not accepted by areDatesBetween()");
        } else {
            System.out.println("FAIL: Invalid times accepted by areDatesBetween()");
        }
    }

    /**
     * Uses a regex with format `yyyy:MM:dd` to check if a string matches the regex
     * @param date date to check
     * @return true if date fits the regex, false if it does not
     */
    public static boolean isValidDate(String date) {
        String dateRegex = "\\d{4}/\\d{2}/\\d{2}";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(date);
        return matcher.matches();
    }

    /**
     * Uses a regex with format `HH:mm` to check if a string matches the regex
     * @param time time to check
     * @return true if `time` fits the regex, false if it does not.
     */
    public static boolean isValidTime(String time) {
        String timeRegex = "\\d{2}:\\d{2}";
        Pattern pattern = Pattern.compile(timeRegex);
        Matcher matcher = pattern.matcher(time);
        return matcher.matches();
    }


    /**
     * Accepts a date and a time and converts them into a UTC Timestamp. Parameters must be set as system default time.
     * @param date date to convert formatted `yyyy/MM/dd`
     * @param time time to convert formatted `hh:mm`
     * @return Timestamp in UTC
     */
    public static Timestamp formValueToTimestamp(String date, String time) {
        String dateFormatted = date.replace("/", "-");
        String timeFormatted = time.concat(":00");
        Timestamp timestamp = Timestamp.valueOf(dateFormatted + " " + timeFormatted);

        LocalDateTime ldt = timestamp.toLocalDateTime();

        return Timestamp.valueOf(ldt);
    }

    /**
     * Accepts a date and a time and converts them into a UTC Timestamp. Parameters must be set as system default time.
     * @param date date to convert
     * @param time time to convert
     * @return Timestamp in UTC
     */
    public static Timestamp formValueToUTCTimestamp(String date, String time) {
        String dateFormatted = date.replace("/", "-");
        String timeFormatted = time.concat(":00");
        Timestamp timestamp = Timestamp.valueOf(dateFormatted + " " + timeFormatted);

        LocalDateTime ldt = timestamp.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());

        ZonedDateTime utcZdt = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime utcLdt = utcZdt.toLocalDateTime();

        return Timestamp.valueOf(utcLdt);
    }

    /**
     * Accepts a date and a time and converts them into a UTC Timestamp. Parameters must be set as system default time.
     * @param date date to convert
     * @param time time to convert
     * @return Timestamp in UTC
     */
    public static Timestamp formValueToESTTimestamp(String date, String time) {
        String dateFormatted = date.replace("/", "-");
        String timeFormatted = time.concat(":00");
        Timestamp timestamp = Timestamp.valueOf(dateFormatted + " " + timeFormatted);

        LocalDateTime ldt = timestamp.toLocalDateTime();
        ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());

        ZonedDateTime utcZdt = zdt.withZoneSameInstant(ZoneId.of(Constants.BUSINESS_TIMEZONE));
        LocalDateTime utcLdt = utcZdt.toLocalDateTime();

        return Timestamp.valueOf(utcLdt);
    }



    /**
     * Generates a Timestamp in UTC time right now
     * @return Timestamp in UTC
     */
    public static Timestamp UTCTimestampNow() {
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneid);
        return Timestamp.valueOf(localDateTime);
    }


    /**
     * checks if a Timestamp has the year and month values that match params `year` and `month`
     * @param start Timestamp to compare year and month against
     * @param month month int (0-11) to compare against Timestamp's month value
     * @param year year (yyyy) to compare against Timestamp's year value
     * @return true if Timestamp month/year match `month` `year`, false if either/both do not match
     */
    public static boolean checkMonthYear(Timestamp start, int month, int year) {
        LocalDateTime ldt = start.toLocalDateTime();
        int checkYear = ldt.getYear();
        int checkMonth = ldt.getMonthValue();

        return month == checkMonth && year == checkYear;
    }

    /**
     * checks that a Timestamp starts after the start of a new week and before the end of a new week
     * @param timestamp Timestamp to check
     * @param selectedWeekStartDate String in format "yyyy/MM/dd HH:mm:ss" representing the start of a week to check
     * @return true if `timestamp` falls between the start/end of `selectedWeekStartDate`, false otherwise
     */
    public static boolean checkWeek(Timestamp timestamp, String selectedWeekStartDate) {
        Timestamp weekStart = Timestamp.valueOf(selectedWeekStartDate + " 00:00:00");
        int daysToMoveForward = 7;
        LocalDate localDate = weekStart.toLocalDateTime().toLocalDate().plusDays(daysToMoveForward);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, weekStart.toLocalDateTime().toLocalTime());
        Timestamp weekEnd = Timestamp.valueOf(localDateTime);

        return timestamp.after(weekStart) && timestamp.before(weekEnd);
    }

    /**
     * converts a Timestamp to a format `yyyy/MM/dd HH:mm` to be seen in a TableView
     * @param timestamp Timestamp to convert
     * @return String in format `yyyy/MM/dd HH:mm`
     */
    public static String TimestampToTableValue(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(timestamp);
        String time = timeFormat.format(timestamp);
        return date + " " + time;
    }

    /**
     * checks that start/end Times are between required start/end Times. All Times must be the same time zone and cannot overlap with midnight.
     * @param start Time representing the proposed start time
     * @param end Time representing the proposed end time
     * @param requiredStart Time required for `start` to fall in front of
     * @param requiredEnd Time required for `end` to fall behind
     * @return true if `start` and `end` fall between `requiredStart` and `requiredEnd`, false if they don't
     */
    public static boolean areDatesBetween(Time start, Time end, Time requiredStart, Time requiredEnd) {

        return start.after(requiredStart) && end.before(requiredEnd);
    }

    public static Timestamp zonedToUTCTimestamp(ZonedDateTime now) {
        ZonedDateTime nowUTC = now.withZoneSameInstant(ZoneId.of("UTC"));
        LocalDateTime nowUTCLocal = nowUTC.toLocalDateTime();
        return Timestamp.valueOf(nowUTCLocal);
    }
}
