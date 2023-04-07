package com.example.appointment_schedule.dao.user;


import com.example.appointment_schedule.model.User;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface UserDAO {
    public ObservableList<User> getAllUsers() throws SQLException;
    public User getUserByUsernamePw(String username, String password) throws SQLException;
    public void addUser(User user) throws SQLException;
    public void deleteUser(User user) throws SQLException;
    public void updateUser(User user) throws SQLException;
}
