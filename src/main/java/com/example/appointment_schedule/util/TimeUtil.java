package com.example.appointment_schedule.util;


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
        String date = "2023/04/06";
        String time = "15:30";
        System.out.println("Resultant Timestamp: " +formValueToUTCTimestamp(date, time));
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
     * @param date date to convert
     * @param time time to convert
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
     * Generates a Timestamp in UTC time right now
     * @return Timestamp in UTC
     */
    public static Timestamp UTCTimestampNow() {
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneid);
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * Checks if a LocalDateTime falls between business hours of 8AM - 10PM EST
     * @param ldt the LocalDateTime to check
     * @return true if time falls within business hours, false if it does not.
     */
    public static boolean LdtWithinBusinessHours(LocalDateTime ldt) {
        ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.of("America/New_York"));

        int hour = zonedDateTime.getHour();

        return (hour >= 8 && hour < 22);
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

    public static boolean checkWeek(Timestamp start, String selectedWeekStartDate) {
        Timestamp weekStart = Timestamp.valueOf(selectedWeekStartDate + " 00:00:00");
        int daysToMoveForward = 7;
        LocalDate localDate = weekStart.toLocalDateTime().toLocalDate().plusDays(daysToMoveForward);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, weekStart.toLocalDateTime().toLocalTime());
        Timestamp weekEnd = Timestamp.valueOf(localDateTime);

        return start.after(weekStart) && start.before(weekEnd);
    }

    public static String TimestampToTableValue(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String date = dateFormat.format(timestamp);
        String time = timeFormat.format(timestamp);
        return date + " " + time;
    }
}
