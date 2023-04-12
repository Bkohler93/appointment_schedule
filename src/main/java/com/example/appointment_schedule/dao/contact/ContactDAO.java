package com.example.appointment_schedule.dao.contact;


import com.example.appointment_schedule.model.Contact;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * interface for accessing entities from the Contact table from a database.
 * @author Brett Kohler
 */
public interface ContactDAO {
    ObservableList<Contact> getAllContacts() throws SQLException;
    Contact getContactById(int id) throws SQLException;
    Contact getContactByName(String name) throws SQLException;
    int getContactIdByName(String value) throws SQLException;
}
