package com.example.appointment_schedule.dao.user;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAOImpl implements UserDAO{
    private ObservableList<User> users = FXCollections.observableArrayList();
    @Override
    public ObservableList<User> getAllUsers() throws SQLException {
        String sql = "select * from Users";
        Query.makeQuery(sql);

        User userResult;
        ResultSet result = Query.getResult();
        try {
            while (result.next()) {
                int id = result.getInt("User_ID");
                String usrname = result.getString("User_Name");
                String password = result.getString("Password");
                Timestamp createDate = result.getTimestamp("Create_Date");
                String createdBy = result.getString("Created_By");
                Timestamp lastUpdate = result.getTimestamp("Last_Update");
                String lastUpdatedBy = result.getString("Last_Updated_By");
                userResult = new User(id, usrname, password, createDate, createdBy, lastUpdate, lastUpdatedBy);
                users.add(userResult);
            }
            return users;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

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

    @Override
    public void deleteUser(User user) throws SQLException {
        String username = user.getUserName();

        String sql = "delete from User where User_Name = '" + username + "'";

        Query.makeQuery(sql);
    }

    @Override
    public void updateUser(User user) throws SQLException {
        int id = user.getId();
        String username = user.getUserName();
        String password = user.getPassword();
        Timestamp createDate = user.getCreateDate();
        String createdBy = user.getCreatedBy();
        Timestamp lastUpdate = user.getLastUpdate();
        String lastUpdatedBy = user.getLastUpdatedBy();

        String sql = "update Users set User_Name = '" + username + "', Password = '" + password + "', Create_Date = '" + createDate
                + "', Created_By = '" + createdBy + "', Last_Update = '" + lastUpdate + "', Last_Updated_By = '" + lastUpdatedBy
                + "' where User_ID = " + id;

        Query.makeQuery(sql);
    }
}
