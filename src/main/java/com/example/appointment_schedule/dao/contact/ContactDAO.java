package com.example.appointment_schedule.dao.contact;


import com.example.appointment_schedule.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public interface ContactDAO {
    public ObservableList<Contact> getAllContacts() throws SQLException;
    public void addContact(Contact contact);
    public void deleteContact(Contact contact);
    public void updateContact(Contact contact);
    public Contact getContactById(int id) throws SQLException;
    public Contact getContactByName(String name) throws SQLException;
    public int getContactIdByName(String value) throws SQLException;

}
