package com.example.appointment_schedule.auth;


import com.example.appointment_schedule.model.User;

/**
 * class is used to share the User object corresponding to the user logged in across pages.
 * `login()` should only be called with a user received from database retrieved using login credentials
 * provided by the user.
 */
public class Auth {
    public static User user;

    public static void login(User validUser) { user = validUser; }

    public static User getUser() { return user; }
}