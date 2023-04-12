package com.example.appointment_schedule.dao.customer;


import com.example.appointment_schedule.model.Customer;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * interface for accessing entities from the Customer table in a database.
 * @author Brett Kohler
 */
public interface CustomerDAO {
    ObservableList<Customer> getAllCustomers() throws SQLException;
    void deleteCustomer(Customer customer) throws SQLException;
    void updateCustomer(Customer customer) throws SQLException;
    void addCustomer(Customer customer) throws SQLException;
    int getNextId() throws SQLException;
}
