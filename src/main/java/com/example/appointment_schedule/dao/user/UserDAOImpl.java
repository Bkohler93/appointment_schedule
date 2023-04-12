package com.example.appointment_schedule.dao.user;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * implementation for UserDAO to retrieve User data from a MySQL database
 * @author Brett Kohler
 */
public class UserDAOImpl implements UserDAO{

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
}
