package com.example.appointment_schedule.dao.user;


import com.example.appointment_schedule.model.User;

import java.sql.SQLException;

/**
 * interface for accessing entities from the User table in a database.
 * @author Brett Kohler
 */
public interface UserDAO {
    User getUserByUsernamePw(String username, String password) throws SQLException;
    void addUser(User user) throws SQLException;
}
