package com.example.appointment_schedule.dao.appointment;

import com.example.appointment_schedule.dao.Query;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 * implementation of AppointmentDAO for accessing the Appointment Table from a SQL database.
 */
public class AppointmentDAOImpl implements AppointmentDAO {
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    /**
     * retrieve all appointments from database
     * @throws SQLException thrown when error with SQL commands
     */
    @Override
    public void getAllAppointments() throws SQLException {
        String sql = "select * from Appointments";
        Query.makeQuery(sql);

        appointments.clear();
        ResultSet result = Query.getResult();
        fillAppointments(result);
    }

    /**
     * retrieve all appointments for a specific customer
     * @param customer customer to retreve appointments for
     * @return observable list of appointments to be used in fxml components
     * @throws SQLException thrown when error with SQL commands
     */
    @Override
    public ObservableList<Appointment> getAllCustomerAppointments(Customer customer) throws SQLException {
        if (appointments.size() == 0) {
            getAllAppointments();
        }
        return appointments.filtered(a -> a.getCustomerId() == customer.getId());
    }

    /**
     * deletes an appointment specified by `appointment` parameter
     * @param appointment appointment to delete
     * @throws SQLException thrown when invalid SQL command
     */
    @Override
    public void deleteAppointment(Appointment appointment) throws SQLException {
        String sql = "delete from Appointments where Appointment_ID = " + appointment.getId();
        Query.makeQuery(sql);

        appointments.remove(appointment);
    }

    /**
     * updates an appointment specified by `appointment` parameter
     * @param appointment appointment to delete
     */
    @Override
    public void updateAppointment(Appointment appointment) throws SQLException {
        int appointmentId = appointment.getId();
        String title = appointment.getTitle();
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String type = appointment.getType();
        Timestamp start = appointment.getStart();
        Timestamp end = appointment.getEnd();
        Timestamp createdDate = appointment.getCreateDate();
        String createdBy = appointment.getCreatedBy();
        Timestamp lastUpdate = appointment.getLastUpdate();
        String lastUpdatedBy = appointment.getLastUpdatedBy();
        int customerId = appointment.getCustomerId();
        int userId = appointment.getUserId();
        int contactId = appointment.getContactId();

        String sql = "update Appointments set Title = '" + title + "', Description = '" + description + "', Location = '" +
                location + "', Type = '" + type + "', Start = '" + start + "', End = '" + end + "', Create_Date = '" + createdDate +
                "', Created_By = '" + createdBy + "', Last_Update = '" + lastUpdate + "', Last_Updated_By = '" + lastUpdatedBy +
                "', Customer_ID = " + customerId + ", User_ID = " + userId + ", Contact_ID = " + contactId + " where Appointment_ID = " + appointmentId;

        Query.makeQuery(sql);
    }

    /**
     * adds appointment to database
     * @param appointment appointment to add
     */
    @Override
    public void addAppointment(Appointment appointment) throws SQLException {
        String title = appointment.getTitle();
        String description = appointment.getDescription();
        String location = appointment.getLocation();
        String type = appointment.getType();
        Timestamp start = appointment.getStart();
        Timestamp end = appointment.getEnd();
        Timestamp createdDate = appointment.getCreateDate();
        String createdBy = appointment.getCreatedBy();
        Timestamp lastUpdate = appointment.getLastUpdate();
        String lastUpdatedBy = appointment.getLastUpdatedBy();
        int customerId = appointment.getCustomerId();
        int userId = appointment.getUserId();
        int contactId = appointment.getContactId();

        String sql = "insert into Appointments (Title, Description, Location, Type, Start, End, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                "values('" + title + "', '" + description + "', '" + location + "', '" + type + "', '" +
                start + "', '" + end + "', '" + createdDate + "', '" + createdBy + "', '" + lastUpdate +
                "', '" + lastUpdatedBy + "', " + customerId + ", " + userId + ", " + contactId + ")";
        Query.makeQuery(sql);

        appointments.add(appointment);
    }

    /**
     * checks whether the database has any appointments with a `start` or `end` that falls between `startDateTime`/`endDateTime`.
     * @param startDateTime start time to check for conflict (must be UTC).
     * @param endDateTime end time to check for conflict (must be UTC).
     * @return true if the proposed `startDateTime` / `endDateTime` conflicts with any other appointment, false if not.
     */
    @Override
    public boolean hasOverlappingAppointments(Timestamp startDateTime, Timestamp endDateTime) throws SQLException {
        String sql = "select * from Appointments where (Start between '" + startDateTime + "' and '" + endDateTime + "') " +
                "or  (End between '" + startDateTime + "' and '" + endDateTime + "')";
        Query.makeQuery(sql);

        ResultSet resultSet = Query.getResult();
        return resultSet.next();
    }

    /**
     * checks whether the database has any appointments with a `start` or `end` that falls between `startDateTime`/`endDateTime`.
     * Excludes appointment that matches `appointmentToExclude`'s ID.
     * @param startDateTime start time to check for conflict (must be UTC).
     * @param endDateTime end time to check for conflicts (must be UTC).
     * @param appointmentToExclude appointment to exclude in check for conflicts
     * @return true if no appointments conflict with proposed times, false if not.
     * @throws SQLException thrown by AppointmentDAO from invalid SQL syntax.
     */
    @Override
    public boolean hasOverlappingAppointments(Timestamp startDateTime, Timestamp endDateTime, Appointment appointmentToExclude) throws SQLException {
        String sql = "select * from Appointments where ((Start between '" + startDateTime + "' and '" + endDateTime + "') " +
                "or  (End between '" + startDateTime + "' and '" + endDateTime + "')) and Appointment_ID != " + appointmentToExclude.getId();
        Query.makeQuery(sql);

        ResultSet resultSet = Query.getResult();
        return resultSet.next();
    }

    /**
     * retrieve an appointment that is within 15 minutes of user login time.
     * @return Appointment within 15 minutes of current time or null if no appointment
     * @throws SQLException throws from invalid values in SQL command
     */
    @Override
    public Appointment getUpcomingAppointment() throws SQLException {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nowPlusFifteenMin = now.plusMinutes(15);
        Timestamp nowTimestampUTC = TimeUtil.zonedToUTCTimestamp(now);
        Timestamp nowPlusFifteenTimestampUTC = TimeUtil.zonedToUTCTimestamp(nowPlusFifteenMin);

        String sql = "select * from Appointments where (Start between '" + nowTimestampUTC + "' and '" + nowPlusFifteenTimestampUTC + "')";
        Query.makeQuery(sql);

        ResultSet result = Query.getResult();
        if (result.next()) {
            int id = result.getInt("Appointment_ID");
            String title = result.getString("Title");
            String description = result.getString("Description");
            String location = result.getString("Location");
            String type = result.getString("Type");
            Timestamp start  = result.getTimestamp("Start");
            Timestamp end  = result.getTimestamp("End");
            Timestamp createDate  = result.getTimestamp("Create_Date");
            String createdBy  = result.getString("Created_By");
            Timestamp lastUpdate = result.getTimestamp("Last_Update");
            String lastUpdatedBy = result.getString("Last_Updated_By");
            int customerId = result.getInt("Customer_ID");
            int userId = result.getInt("User_ID");
            int contactId = result.getInt("Contact_ID");
            return new Appointment(id, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
        }
        return null;
    }

    /**
     * retrieves appointments with start times in a given month/year
     * @param month month of appointment to retrieve
     * @param year year of appointment to retrieve
     * @return observable list of appointments to be used in fxml components
     */
    @Override
    public ObservableList<Appointment> getAppointmentsByMonthYear(int month, int year)  {
        return appointments.filtered(a -> TimeUtil.checkMonthYear(a.getStart(), month, year));
    }

    /**
     * retrieves appointments with start date during a week specified by `selectedWeekStartDate`
     * @param selectedWeekStartDate String representing the starting date's week formatted as `yyyy-MM-dd`
     * @return observable list of appointments to be used in fxml components
     */
    @Override
    public ObservableList<Appointment> getAppointmentsByWeek(String selectedWeekStartDate)  {
        return appointments.filtered(a -> TimeUtil.checkWeek(a.getStart(), selectedWeekStartDate));
    }

    /**
     * finds the next available ID for Appointment table
     * @return the id as an int
     * @throws SQLException from invalid SQL syntax
     */
    @Override
    public int getNextId() throws SQLException {
        String sql = "SELECT MAX(Appointment_ID) + 1 AS next_id FROM Appointments";
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

    /**
     * returns a _distinct_ list of Type names from all appointments
     * @return the list of Types (string)
     */
    @Override
    public ObservableList<String> getUniqueTypeNames() throws SQLException {
        if (appointments.size() == 0) {
            getAllAppointments();
        }
        return appointments.stream().map(Appointment::getType).distinct().collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * gets all appointments whose type matches the parameter `type`
     * @param type type of appointment to retrieve
     * @return List of appointments
     * @throws SQLException throws from `getAllAppointments()` call
     */
    @Override
    public ObservableList<Appointment> getAppointmentsByType(String type) throws SQLException {
        if (appointments.size() == 0) {
            getAllAppointments();
        }
       return appointments.stream().filter(a -> a.getType().equals(type)).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * returns a list of appointments for a contact specified by `selectedContact`
     * @param selectedContact the contact to look for appointments for
     * @return list of contact's appointments
     * @throws SQLException thrown by `getAllAppointments()`
     */
    @Override
    public ObservableList<Appointment> getAllContactAppointments(Contact selectedContact) throws SQLException {
        if (appointments.size() == 0) {
            getAllAppointments();
        }
       return appointments.stream().filter(a -> a.getContactId() == selectedContact.getId()).collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    /**
     * fills the `appointments` class attribute with a specified ResultSet containing appointments to add.
     * @param result ResultSet to retrieve appointments from
     * @throws SQLException thrown if invalid retrieval from ResultSet
     */
    private void fillAppointments(ResultSet result) throws SQLException {
        try {
            Appointment appointmentResult;
            while (result.next()) {
                int id = result.getInt("Appointment_ID");
                String title = result.getString("Title");
                String description = result.getString("Description");
                String location = result.getString("Location");
                String type = result.getString("Type");
                Timestamp start  = result.getTimestamp("Start");
                Timestamp end  = result.getTimestamp("End");
                Timestamp createDate  = result.getTimestamp("Create_Date");
                String createdBy  = result.getString("Created_By");
                Timestamp lastUpdate = result.getTimestamp("Last_Update");
                String lastUpdatedBy = result.getString("Last_Updated_By");
                int customerId = result.getInt("Customer_ID");
                int userId = result.getInt("User_ID");
                int contactId = result.getInt("Contact_ID");
                appointmentResult = new Appointment(id, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                appointments.add(appointmentResult);
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
