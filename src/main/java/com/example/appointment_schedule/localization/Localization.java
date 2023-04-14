package com.example.appointment_schedule.localization;


import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Used to translate text to the system default language. Initialize must be called before attempting to translate
 * text using `text()` method.
 * @author Brett Kohler
 */
public class Localization {
    private static boolean isActive;
    private static ResourceBundle rb;

    /**
     * Initializes the resource bundle to match the default Locale for system. If no resource bundle is found
     * for the language the application is displayed in English.
     */
    public static void initialize() {
        try {
            rb = ResourceBundle.getBundle("com/example/appointment_schedule/Nat", Locale.getDefault());
            if (Locale.getDefault().getLanguage().equals("fr")) {
                System.out.println("Application language set to French");
                isActive = true;
            } else {
                isActive = false;
                System.out.println("Application language set to English");
            }
        } catch (MissingResourceException e) {
            isActive = false;
            System.out.println("Unable to get Resource bundle\t" + e);
        }
    }

    /**
     * Translates a string from English to the language the ResourceBundle is configured to use. Words to translate must
     * only be the words in the string, no punctuation, unless a key in the configured ResourceBundle properties file
     * is set to include punctuation. If a word has no key value, the English word will be used.
     * Capitalization will match the `original` string.
     * @param original string to translate
     * @return translated string
     */
    public static String text(String original) {
        if (isActive) {
            String[] strings = original.split(" ");
            String result = "";
            for (String s : strings) {
                if (Character.isUpperCase(s.charAt(0))) {
                    try {
                        String localizedString = rb.getString(s.toLowerCase());
                        result += localizedString.substring(0, 1).toUpperCase() + localizedString.substring(1) + " ";
                    } catch (MissingResourceException e) {
                        result += s + " ";
                    }
                } else {
                    result += rb.getString(s) + " ";
                }
            }
            return result;
        } else {
            return original;
        }
    }
}
