package com.example.appointment_schedule.dao.customer;


import com.example.appointment_schedule.model.Customer;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface CustomerDAO {
    public ObservableList<Customer> getAllCustomers() throws SQLException;
    public void deleteCustomer(Customer customer) throws SQLException;
    public void updateCustomer(Customer customer) throws SQLException;
    public void addCustomer(Customer customer) throws SQLException;
    public int getNextId() throws SQLException;
}
