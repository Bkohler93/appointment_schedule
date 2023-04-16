package com.example.appointment_schedule.dao.user;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * implementation for UserDAO to retrieve User data from a MySQL database
 * @author Brett Kohler
 */
public class UserDAOImpl implements UserDAO{

    private final ObservableList<User> users = FXCollections.observableArrayList();

    /**
     * retrieve User entity with matching `username` and `password`
     * @param username username to search for
     * @param password password to search for
     * @return User retrieved
     * @throws SQLException thrown by invalid SQL syntax
     */
    @Override
    public User getUserByUsernamePw(String username, String password) throws SQLException {
        String sql = "select * from Users where User_Name = '" + username + "' and Password = '" + password + "'";
        Query.makeQuery(sql);

        User userResult;
        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("User_ID");
            String usrname = result.getString("User_Name");
            String pw = result.getString("Password");
            Timestamp createDate = result.getTimestamp("Create_Date");
            String createdBy = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            userResult = new User(id, usrname, pw, createDate, createdBy, lastUpdate, lastUpdatedBy);
            return userResult;
        }
        return null;
    }

    /**
     * add user to database
     * @param user user to add
     * @throws SQLException thrown by invalid SQL syntax
     */
    @Override
    public void addUser(User user) throws SQLException {
        String username = user.getUserName();
        String password = user.getPassword();
        Timestamp createDate = user.getCreateDate();
        String createdBy = user.getCreatedBy();
        Timestamp lastUpdate = user.getLastUpdate();
        String lastUpdatedBy = user.getLastUpdatedBy();

        String sql = "insert into Users (User_Name, Password, Create_Date, Created_By, Last_Update, Last_Updated_By)" +
                " values('" + username + "', '" + password + "', '" + createDate + "', '" + createdBy + "', '" + lastUpdate
                + "', '" + lastUpdatedBy + "')";

        Query.makeQuery(sql);
    }

    /**
     * retrieves all users from database. clears users first to ensure list is empty
     * @return list of users to be used in fxml forms
     * @throws SQLException thrown if invalid SQL syntax
     */
    @Override
    public ObservableList<User> getAllUsers() throws SQLException {
        String sql = "select * from users";
        Query.makeQuery(sql);

        users.clear();
        ResultSet result = Query.getResult();
        fillUsers(result);
        return users;
    }

    /**
     * retrieve a user id from a String name
     * @param name  Name of user
     * @return
     */
    @Override
    public int getUserIdByName(String name) throws SQLException {
        if (users.isEmpty()) {
            getAllUsers();
        }

        Optional<User> userResult = users.stream().filter(u -> u.getUserName().equals(name)).findFirst();

        return userResult.map(User::getId).orElse(-1);
    }

    /**
     * fills observable list of users with users from a ResultSet
     * @param result ResultSet to retrieve users from
     */
    private void fillUsers(ResultSet result) {
        try {
            User userResult;
            while (result.next()) {
                int id = result.getInt("User_ID");
                String userName = result.getString("User_Name");
                String password = result.getString("Password");
                Timestamp createDate = result.getTimestamp("Create_Date");
                String createdBy = result.getString("Created_By");
                Timestamp lastUpdate = result.getTimestamp("Last_Update");
                String lastUpdatedBy = result.getString("Last_Updated_By");
                userResult = new User(id, userName, password, createDate, createdBy, lastUpdate, lastUpdatedBy);
                users.add(userResult);
            }
        } catch (SQLException ignored) {
        }
    }
}
