package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.Constants;
import com.example.appointment_schedule.auth.Auth;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.dao.customer.CustomerDAO;
import com.example.appointment_schedule.dao.customer.CustomerDAOImpl;
import com.example.appointment_schedule.dao.user.UserDAO;
import com.example.appointment_schedule.dao.user.UserDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.model.User;
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * functionality used throughout Appointment.fxml.
 * @author Brett Kohler
 */
public class AppointmentController implements Initializable {


    private Customer customer;
    private Appointment appointment;
    private final ContactDAO contactDAO = new ContactDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    private ComboBox<String> customerIdComboBox;
    @FXML
    private ComboBox<String> userIdComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<LocalTime> startTimeComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<LocalTime> endTimeComboBox;
    @FXML
    private TextField typeTextField;
    @FXML
    private Text appointmentFormTitleText;
    @FXML
    private TextField idTextField;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private ComboBox<String> contactIdComboBox;
    @FXML
    private Text infoDisplayText;
    @FXML
    private VBox formContainerVBox;


    /**
     * When adding a new appointment for a specific customer, the customer must be known
     * to fill in Customer_ID and prevent it from being changed
     * @param customer customer to send
     */
    public void sendCustomer(Customer customer) throws SQLException {
        this.customer = customer;
        fillForm();
    }

    /**
     * When modifying a customer's appointment, the controller must know both
     * the customer and appointment to fill in each of their respective IDs.
     * @param customer customer to send
     * @param appointment appointment to send
     */
    public void sendCustomerAndAppointment(Customer customer, Appointment appointment) {
        this.customer = customer;
        this.appointment = appointment;
    }

    /**
     * fills form based on what is being performed with the form (updating vs adding appointments)
     * @throws SQLException thrown by DAOs when SQL command fails
     */
    private void fillForm() throws SQLException {

        // set ID combo boxes with names instead of int IDs
        contactIdComboBox.setItems(contactDAO.getAllContacts().stream().map(Contact::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        userIdComboBox.setItems(userDAO.getAllUsers().stream().map(User::getUserName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        customerIdComboBox.setItems(customerDAO.getAllCustomers().stream().map(Customer::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        idTextField.setDisable(true);

        // set up TimeComboBoxes
        startTimeComboBox.setPromptText("Select a time");
        endTimeComboBox.setPromptText("Select a time");

        // fill in TimeComboBoxes with correct time intervals
        for (int hours = 0; hours < 24; hours++) {
            for (int minutes = 0; minutes < 60; minutes += 15) {
                startTimeComboBox.getItems().add(LocalTime.of(hours, minutes));
                endTimeComboBox.getItems().add(LocalTime.of(hours, minutes));
            }
        }

        // modifying a customer's appointment
        if (customer != null && appointment != null) {

            // fill TextFields/Text/ComboBoxes
            appointmentFormTitleText.setText("Modify " + customer.getName() + "'s Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactIdComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            customerIdComboBox.getSelectionModel().select(customerDAO.getCustomerById(appointment.getCustomerId()).getName());
            userIdComboBox.getSelectionModel().select(Auth.getUser().getUserName());
            typeTextField.setText(appointment.getType());
            customerIdComboBox.setDisable(true);
            contactIdComboBox.setDisable(true);
            userIdComboBox.setDisable(true);

            Timestamp startTimestamp = appointment.getStart();
            LocalDateTime startLDT = startTimestamp.toLocalDateTime();
            startDatePicker.setValue(startLDT.toLocalDate());
            startTimeComboBox.getSelectionModel().select(startLDT.toLocalTime());

            Timestamp endTimestamp = appointment.getEnd();
            LocalDateTime endLDT = endTimestamp.toLocalDateTime();
            endDatePicker.setValue(endLDT.toLocalDate());
            endTimeComboBox.getSelectionModel().select(endLDT.toLocalTime());
        }

        // fill form fields when form is used for specific customer but no specific appointment (Adding an appointment for
        // specified customer)
        else if (appointment == null && customer != null) {
            appointmentFormTitleText.setText("Add Appointment For " + customer.getName());
            idTextField.setText(Integer.toString(appointmentDAO.getNextId()));
            customerIdComboBox.getSelectionModel().select(customerDAO.getCustomerById(appointment.getCustomerId()).getName());
            customerIdComboBox.setDisable(true);
        }

        // fill form fields when form is used for specific appointment  (Modifying an existing appointment)
        else if (appointment != null) {
            appointmentFormTitleText.setText("Modify Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));

            // fill form fields with specific appointment data
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactIdComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            typeTextField.setText(appointment.getType());

            Timestamp startTimestamp = appointment.getStart();
            LocalDateTime startLDT = startTimestamp.toLocalDateTime();
            startDatePicker.setValue(startLDT.toLocalDate());
            startTimeComboBox.getSelectionModel().select(startLDT.toLocalTime());

            Timestamp endTimestamp = appointment.getEnd();
            LocalDateTime endLDT = endTimestamp.toLocalDateTime();
            endDatePicker.setValue(endLDT.toLocalDate());
            endTimeComboBox.getSelectionModel().select(endLDT.toLocalTime());

            // fill ID TextFields
            customerIdComboBox.getSelectionModel().select(customerDAO.getCustomerById(appointment.getCustomerId()).getName());
            userIdComboBox.getSelectionModel().select(Auth.getUser().getUserName());
        }

        // adding an appointment (No specified appointment or customer)
        else  {
            appointmentFormTitleText.setText("Add Appointment");
            idTextField.setText(Integer.toString(appointmentDAO.getNextId()));
        }
    }


    /**
     * When modifying an appointment, controller needs to know which appointment is being modified to fill in Appointment_ID
     * @param appointment appointment to send to controller
     */
    public void sendAppointment(Appointment appointment) throws SQLException {
        this.appointment = appointment;
        fillForm();
    }

    /**
     * Executed on form first loading
     * @param url not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FxUtil.applyEventHandlersToTextFields(formContainerVBox, infoDisplayText);
            fillForm();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * closes current window after user clicks on Cancel button
     * @param actionEvent event propagated from user click
     */
    @FXML
    public void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * Uses Appointment.fxml's TextFields to create an appointment saves the new/updated appointment.
     * @param actionEvent event sent from pressing Save Button on Appointment.fxml
     * @throws SQLException thrown by appointmentDAO
     */
    @FXML
    public void saveAppointment(ActionEvent actionEvent) throws SQLException {

        // retrieve inputs from form fields
        int id = Integer.parseInt(idTextField.getText());
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String type = typeTextField.getText();
        String createdBy = appointment == null ? Auth.getUser().getCreatedBy() : appointment.getCreatedBy();
        String updatedBy = Auth.getUser().getUserName();
        int customerId = customerDAO.getCustomerIdByName(customerIdComboBox.getValue());
        int userId = userDAO.getUserIdByName(userIdComboBox.getValue());
        int contactId = contactDAO.getContactIdByName(contactIdComboBox.getValue());

        // generate create/update timestamps
        Timestamp createTimestamp = appointment == null ? TimeUtil.UTCTimestampNow() : appointment.getCreateDate();
        Timestamp updateTimestamp = TimeUtil.UTCTimestampNow();

        // generate start timestamp in UTC and EST
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();
        ZonedDateTime startZDT = startDate.atTime(startTime).atZone(ZoneId.systemDefault());
        Timestamp start = TimeUtil.zonedToUTCTimestamp(startZDT);
        Timestamp startEST = TimeUtil.zonedToESTTimestamp(startZDT);

        // generate end timestamp in UTC and EST
        LocalDate endDate = endDatePicker.getValue();
        LocalTime endTime = endTimeComboBox.getValue();
        ZonedDateTime endZDT = endDate.atTime(endTime).atZone(ZoneId.systemDefault());
        Timestamp end = TimeUtil.zonedToUTCTimestamp(endZDT);
        Timestamp endEST = TimeUtil.zonedToESTTimestamp(endZDT);

        // check if proposed appointment times conflict with business hours
        if (TimeUtil.hasConflictingTimes(Time.valueOf(startEST.toLocalDateTime().toLocalTime()), Time.valueOf(endEST.toLocalDateTime().toLocalTime()), Constants.BUSINESS_HOURS_START_EST, Constants.BUSINESS_HOURS_END_EST)) {
            FxUtil.displayInfoDisplayText("One or more proposed appointment times are outside of business hours (8am-10pm EST).", true, infoDisplayText);
            return;
        }

        // check if proposed appointment times conflict with other appointment times (exclude modifying appointment if there is one)
        if (appointment == null) {
            if (appointmentDAO.hasOverlappingAppointments(start, end)) {
                FxUtil.displayInfoDisplayText("The proposed appointment time conflicts with other appointments.", true, infoDisplayText);
                return;
            }
        } else {
            if (appointmentDAO.hasOverlappingAppointments(start, end, appointment)) {
                FxUtil.displayInfoDisplayText("The proposed appointment time conflicts with other appointments.", true, infoDisplayText);
                return;
            }
        }

        // create new appointment with retrieved form values
        Appointment newAppointment = new Appointment(id, title, description, location, type, start, end, createTimestamp, createdBy, updateTimestamp, updatedBy, customerId, userId, contactId);

        // determine whether to add appointment as a new appointment or update the old appointment
        if (appointment == null) {
            appointmentDAO.addAppointment(newAppointment);
        } else {
            appointmentDAO.updateAppointment(newAppointment);
        }

        // exit back to previous form
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}

