package com.example.appointment_schedule.dao.appointment;


import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Customer;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface AppointmentDAO {
    public ObservableList<Appointment> getAllAppointments() throws SQLException;
    public ObservableList<Appointment> getAllCustomerAppointments(Customer customer) throws SQLException;
    public void deleteAppointment(Appointment appointment) throws SQLException;
    public void updateAppointment(Appointment appointment) throws SQLException;
    public void addAppointment(Appointment appointment) throws SQLException;

    public ObservableList<Appointment> getAppointmentsByMonthYear(int month, int year) throws SQLException;

    public ObservableList<Appointment> getAppointmentsByWeek(String selectedWeekStartDate) throws SQLException;

    int getNextId() throws SQLException;
}
