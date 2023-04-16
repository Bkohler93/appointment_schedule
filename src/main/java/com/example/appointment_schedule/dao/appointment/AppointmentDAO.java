package com.example.appointment_schedule.dao.appointment;


import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.Customer;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * interface that templates a data access object for a data source.
 * @author Brett Kohler
 */
public interface AppointmentDAO {
    void getAllAppointments() throws SQLException;
    ObservableList<Appointment> getAllCustomerAppointments(Customer customer) throws SQLException;
    void deleteAppointment(Appointment appointment) throws SQLException;
    void updateAppointment(Appointment appointment) throws SQLException;
    void addAppointment(Appointment appointment) throws SQLException;

    boolean hasOverlappingAppointments(Timestamp startDateTime, Timestamp endDateTime) throws SQLException;
    boolean hasOverlappingAppointments(Timestamp startDateTime, Timestamp endDateTime, Appointment appointmentToExclude) throws SQLException;
    Appointment getUpcomingAppointment() throws SQLException;

    ObservableList<Appointment> getAppointmentsByMonthYear(int month, int year) throws SQLException;

    ObservableList<Appointment> getAppointmentsByWeek(String selectedWeekStartDate) throws SQLException;

    int getNextId() throws SQLException;

    ObservableList<String> getUniqueTypeNames() throws SQLException;

    ObservableList<Appointment> getAppointmentsByType(String type) throws SQLException;

    ObservableList<Appointment> getAllContactAppointments(Contact selectedContact) throws SQLException;
}
