package com.example.appointment_schedule.dao.contact;


import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/** LAMBDA EXPRESSIONS HERE!
 * implementation for ContactDAO to retrieve data from Contact table in a MySQL database
 * @author Brett Kohler
 */
public class ContactDAOImpl implements ContactDAO{
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    /**
     * retrieve all contacts from Contacts table
     * @return list of contacts
     * @throws SQLException throws from invalid SQL query
     */
    @Override
    public ObservableList<Contact> getAllContacts() throws SQLException {
        String sql = "select * from contacts";
        Query.makeQuery(sql);

        contacts.clear();
        ResultSet result = Query.getResult();
        fillContacts(result);
        return contacts;
    }

    /**
     * uses a ResultSet to retrieve all Contacts instances and fills the class' list of contacts
     * @param result ResultSet to retrieve fields from
     */
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
        } catch (SQLException ignored) {
        }
    }


    @Override
    public Contact getContactById(int id) throws SQLException {
        if (contacts.isEmpty()) {
            getAllContacts();
        }

        Optional<Contact> contactResult = contacts.stream().filter(c -> c.getId() == id).findFirst();

        return contactResult.orElse(null);
    }

    /**
     * retrieve a single contact by name. Uses a LAMBDA EXPRESSION to efficiently filter contacts for the desired name.
     * @param name name to search for matching contact
     * @return Contact retrieved
     * @throws SQLException thrown by `getAllContacts`
     */
    @Override
    public Contact getContactByName(String name) throws SQLException {
       if (contacts.isEmpty()) {
           getAllContacts();
       }
       Optional<Contact> contactResult = contacts.stream().filter(c -> c.getName().equals(name)).findFirst();

       return contactResult.orElse(null);
    }

    /**
     * retrieve contact id by name. Uses a LAMBDA EXPRESSION to quickly filter contacts for matching contact
     * @param name name to search for
     * @return integer id
     * @throws SQLException thrown by `getAllContacts()`
     */
    @Override
    public int getContactIdByName(String name) throws SQLException {
        if (contacts.isEmpty()) {
            getAllContacts();
        }

        Optional<Contact> contactResult = contacts.stream().filter(c -> c.getName().equals(name)).findFirst();

        return contactResult.map(Contact::getId).orElse(-1);
    }
}
