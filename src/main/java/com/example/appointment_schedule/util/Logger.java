package com.example.appointment_schedule.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Logger {
    /**
     * writes login activity to "login_activity.txt". Times are written in UTC
     * @param username String username to write to file
     * @param password String password to write to file
     * @param isSuccess boolean to determine whether "success" or "failure" is written to file.
     */
    public static void login(String username, String password, boolean isSuccess) {

        String filePath = "login_activity.txt";
        String result = isSuccess ? "success" : "fail";
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));

            LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
            String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            writer.write(username + "\t" + password + "\t" + result + "\t" + formattedTime);
            writer.newLine();

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
