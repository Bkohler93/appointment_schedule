package com.example.appointment_schedule.dao.customer;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * implementation for CustomerDAO to access Customer data from a MySQL database
 * @author Brett Kohler
 */
public class CustomerDAOImpl implements CustomerDAO{
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    /**
     * retrieves all customers from DB and places them into an ObservableList to be used in fxml forms.
     * @return list of customers
     * @throws SQLException thrown by invalid SQL syntax
     */
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

    /**
     * deletes a customer from database
     * @param customer customer to delete
     * @throws SQLException thrown by invalid SQL syntax, invalid id.
     */
    @Override
    public void deleteCustomer(Customer customer) throws SQLException {
        String sql = "delete from Customers where Customer_ID = " + customer.getId();
        Query.makeQuery(sql);
    }

    /**
     * updates a customer with matching id as parameterized `customer`
     * @param customer new customer data to update
     * @throws SQLException thrown by invalid SQL syntax.
     */
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

    /**
     * adds a customer to database
     * @param customer customer to add
     * @throws SQLException thrown by invalid SQL syntax.
     */
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

    /**
     * retrieves the next Customer_ID from database
     * @return the next id
     * @throws SQLException thrown by invalid SQL syntax
     */
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
