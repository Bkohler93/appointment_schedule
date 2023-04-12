package com.example.appointment_schedule.dao.contact;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ContactDAOImpl implements ContactDAO{
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    @Override
    public ObservableList<Contact> getAllContacts() throws SQLException {
        String sql = "select * from contacts";
        Query.makeQuery(sql);

        contacts.clear();
        ResultSet result = Query.getResult();
        fillContacts(result);
        return contacts;
    }

    private void fillContacts(ResultSet result) {
        try {
            Contact contactResult;
            while (result.next()) {
                int id = result.getInt("Contact_ID");
                String name = result.getString("Contact_Name");
                String email = result.getString("Email");
                contactResult = new Contact(id, name, email);
                contacts.add(contactResult);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public void addContact(Contact contact) {

    }

    @Override
    public void deleteContact(Contact contact) {

    }

    @Override
    public void updateContact(Contact contact) {

    }

    @Override
    public Contact getContactById(int id) throws SQLException {
        if (contacts.isEmpty()) {
            getAllContacts();
        }
//        String sql = "select * from Contacts where Contact_ID = " + id;
//        Query.makeQuery(sql);
//
//        Contact contactResult;
//        ResultSet result = Query.getResult();
//
//        if (result.next()) {
//            int contactId = result.getInt("Contact_ID");
//            String name = result.getString("Contact_Name");
//            String email = result.getString("Email");
//            contactResult = new Contact(contactId, name, email);
//            return contactResult;
//        }
//        return null;
        Optional<Contact> contactResult = contacts.stream().filter(c -> c.getId() == id).findFirst();

        return contactResult.orElse(null);
    }

    @Override
    public Contact getContactByName(String name) throws SQLException {
       if (contacts.isEmpty()) {
           getAllContacts();
       }
       Optional<Contact> contactResult = contacts.stream().filter(c -> c.getName() == name).findFirst();

       return contactResult.orElse(null);
    }

    /**
     * USING LAMBDA EXPRESSION TO QUICKLY FILTER
     * @param value
     * @return
     * @throws SQLException
     */
    @Override
    public int getContactIdByName(String value) throws SQLException {
        if (contacts.isEmpty()) {
            getAllContacts();
        }
        return contacts
                .filtered(c -> c.getName().equals(value))
                .stream().findFirst()
                .get()
                .getId();
    }
}
