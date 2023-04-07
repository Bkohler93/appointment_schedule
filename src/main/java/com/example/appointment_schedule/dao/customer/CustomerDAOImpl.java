package com.example.appointment_schedule.dao.customer;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CustomerDAOImpl implements CustomerDAO{
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    @Override
    public ObservableList<Customer> getAllCustomers() throws SQLException {
        String sql = "select * from Customers";
        Query.makeQuery(sql);

        Customer customerResult;
        customers.clear();
        ResultSet result = Query.getResult();
        try {
            while (result.next()) {
                int id = result.getInt("Customer_ID");
                String name = result.getString("Customer_Name");
                String address = result.getString("Address");
                String postalCode = result.getString("Postal_Code");
                String phone = result.getString("Phone");
                Timestamp createDate = result.getTimestamp("Create_Date");
                String createdBy = result.getString("Created_By");
                Timestamp lastUpdate = result.getTimestamp("Last_Update");
                String lastUpdatedBy = result.getString("Last_Updated_By");
                int divisionId = result.getInt("Division_ID");
                customerResult = new Customer(id, name, address, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionId);
                customers.add(customerResult);
            }
            return customers;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void deleteCustomer(Customer customer) throws SQLException {
        String sql = "delete from Customers where Customer_ID = " + customer.getId();
        Query.makeQuery(sql);
    }

    @Override
    public void updateCustomer(Customer customer) throws SQLException {
        int id = customer.getId();
        String name = customer.getName();
        String address = customer.getAddress();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();
        Timestamp createDate = customer.getCreateDate();
        String createdBy = customer.getCreatedBy();
        Timestamp lastUpdate = customer.getLastUpdate();
        String lastUpdatedBy = customer.getLastUpdatedBy();
        int divisionId = customer.getDivisionId();

        String sql = "update Customers set Customer_Name = '" + name + "', Address = '" + address + "', Postal_Code = '"
                + postalCode + "', Phone = '" + phone + "', Create_Date = '" + createDate + "', Created_By = '" + createDate
                + "', Created_By = '" + createdBy + "', Last_Update = '" + lastUpdate + "', Last_Updated_By = '" + lastUpdatedBy
                + "', Division_ID = " + divisionId + " where Customer_ID = " + id;

        Query.makeQuery(sql);
    }

    @Override
    public void addCustomer(Customer customer) throws SQLException {
        String name = customer.getName();
        String address = customer.getAddress();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();
        Timestamp createDate = customer.getCreateDate();
        String createdBy = customer.getCreatedBy();
        Timestamp lastUpdate = customer.getLastUpdate();
        String lastUpdatedBy = customer.getLastUpdatedBy();
        int divisionId = customer.getDivisionId();

        String sql = "insert into Customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID)"
                + " values('" + name + "', '" + address + "', '" + postalCode + "', '" + phone + "', '" + createDate + "', '" + createdBy + "', '"
                + lastUpdate + "', '" + lastUpdatedBy + "', '" + divisionId + "')";

        Query.makeQuery(sql);
    }

    @Override
    public int getNextId() throws SQLException {
        String sql = "SELECT MAX(Customer_ID) + 1 AS next_id FROM Customers";
        Query.makeQuery(sql);

        int resultInt;
        ResultSet result = Query.getResult();

        if (result.next()) {
            resultInt = result.getInt("next_id");
            return resultInt;
        } else {
            throw new SQLException("No ID available");
        }
    }
}
