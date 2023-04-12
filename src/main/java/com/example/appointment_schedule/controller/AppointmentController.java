package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.Constants;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    @FXML
    private TextField typeTextField;
    @FXML
    private TextField startDateTextField;
    @FXML
    private TextField startTimeTextField;
    @FXML
    private TextField endDateTextField;
    @FXML
    private TextField endTimeTextField;
    @FXML
    private TextField customerIdTextField;
    @FXML
    private TextField userIdTextField;
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
    private ComboBox<String> contactComboBox;
    @FXML
    private TextField updatedByTextField;
    @FXML
    private TextField updateTimeTextField;
    @FXML
    private TextField updateDateTextField;
    @FXML
    private TextField createdByTextField;
    @FXML
    private TextField createTimeTextField;
    @FXML
    private TextField createDateTextField;
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

        contactComboBox.setItems(contactDAO.getAllContacts().stream().map(Contact::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        idTextField.setDisable(true);

        // modifying a customer's appointment
        if (customer != null && appointment != null) {

            // fill TextFields/Text/ComboBoxes
            appointmentFormTitleText.setText("Modify " + customer.getName() + "'s Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            typeTextField.setText(appointment.getType());
            createdByTextField.setText(appointment.getCreatedBy());
            customerIdTextField.setText(Integer.toString(customer.getId()));
            customerIdTextField.setDisable(true);
            userIdTextField.setText(Integer.toString(appointment.getUserId()));

            // retrieve and format appointment start/end times
            Timestamp timestampStart = appointment.getStart();
            Timestamp timestampEnd = appointment.getEnd();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(timestampStart);
            String dateEnd = dateFormat.format(timestampEnd);
            String timeStart = timeFormat.format(timestampStart);
            String timeEnd = timeFormat.format(timestampEnd);

            // fill Start and End Date/Time TextFields
            startDateTextField.setText(dateStart);
            startTimeTextField.setText(timeStart);
            endDateTextField.setText(dateEnd);
            endTimeTextField.setText(timeEnd);

            // fill Create Date/Time TextFields
            Timestamp createdTimestamp = appointment.getCreateDate();
            String createdDate = dateFormat.format(createdTimestamp);
            String createdTime = timeFormat.format(createdTimestamp);
            createDateTextField.setText(createdDate);
            createTimeTextField.setText(createdTime);

            // retrieve and fill LastUpdate Date/Time TextFields
            Timestamp updatedTimestamp = appointment.getLastUpdate();
            String updatedDate = dateFormat.format(updatedTimestamp);
            String updatedTime = timeFormat.format(updatedTimestamp);
            updateDateTextField.setText(updatedDate);
            updateTimeTextField.setText(updatedTime);
        }

        // fill form fields when form is used for specific customer but no specific appointment (Adding an appointment for
        // specified customer)
        else if (appointment == null && customer != null) {
            appointmentFormTitleText.setText("Add Appointment For " + customer.getName());
            idTextField.setText(Integer.toString(appointmentDAO.getNextId()));
            customerIdTextField.setText(Integer.toString(customer.getId()));
            customerIdTextField.setDisable(true);
        }

        // fill form fields when form is used for specific appointment  (Modifying an existing appointment)
        else if (appointment != null) {
            appointmentFormTitleText.setText("Modify Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));

            // fill form fields with specific appointment data
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            typeTextField.setText(appointment.getType());

            // retrieve and format TextFields that require Time/Date
            Timestamp timestampStart = appointment.getStart();
            Timestamp timestampEnd = appointment.getEnd();
            Timestamp createdTimestamp = appointment.getCreateDate();
            Timestamp updatedTimestamp = appointment.getLastUpdate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(timestampStart);
            String dateEnd = dateFormat.format(timestampEnd);
            String timeStart = timeFormat.format(timestampStart);
            String timeEnd = timeFormat.format(timestampEnd);
            String createdDate = dateFormat.format(createdTimestamp);
            String createdTime = timeFormat.format(createdTimestamp);
            String updatedDate = dateFormat.format(updatedTimestamp);
            String updatedTime = timeFormat.format(updatedTimestamp);

            // fill Time TextFields
            startDateTextField.setText(dateStart);
            startTimeTextField.setText(timeStart);
            endDateTextField.setText(dateEnd);
            endTimeTextField.setText(timeEnd);
            createDateTextField.setText(createdDate);
            createTimeTextField.setText(createdTime);
            createdByTextField.setText(appointment.getCreatedBy());
            updateDateTextField.setText(updatedDate);
            updateTimeTextField.setText(updatedTime);
            updatedByTextField.setText(appointment.getLastUpdatedBy());

            // fill ID TextFields
            customerIdTextField.setText(Integer.toString(appointment.getCustomerId()));
            userIdTextField.setText(Integer.toString(appointment.getUserId()));
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
        String createdBy = createdByTextField.getText();
        String updatedBy = updatedByTextField.getText();
        String startDate = startDateTextField.getText();
        String startTime = startTimeTextField.getText();
        String endDate = endDateTextField.getText();
        String endTime = endTimeTextField.getText();
        String createDate = createDateTextField.getText();
        String createTime = createTimeTextField.getText();
        String updateDate = updateDateTextField.getText();
        String updateTime = updateTimeTextField.getText();
        int customerId = Integer.parseInt(customerIdTextField.getText());
        int userId = Integer.parseInt(userIdTextField.getText());
        int contactId = contactDAO.getContactIdByName(contactComboBox.getValue());

        // convert times to EST to compare with business hours
        Timestamp start = TimeUtil.formValueToUTCTimestamp(startDate, startTime);
        Timestamp end = TimeUtil.formValueToUTCTimestamp(endDate, endTime);
        Timestamp createTimestamp = TimeUtil.formValueToUTCTimestamp(createDate, createTime);
        Timestamp updateTimestamp = TimeUtil.formValueToUTCTimestamp(updateDate, updateTime);
        Timestamp startEST = TimeUtil.formValueToESTTimestamp(startDate, startTime);
        Timestamp endEST = TimeUtil.formValueToESTTimestamp(endDate, endTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String startTimeEST = timeFormat.format(startEST.getTime());
        String endTimeEST = timeFormat.format(endEST.getTime());

        // check if proposed appointment times conflict with business hours
        if (TimeUtil.hasConflictingTimes(Time.valueOf(startTimeEST), Time.valueOf(endTimeEST), Constants.BUSINESS_HOURS_START_EST, Constants.BUSINESS_HOURS_END_EST)) {
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

