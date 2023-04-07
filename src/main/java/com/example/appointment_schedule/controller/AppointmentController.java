package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AppointmentController implements Initializable {
    public TextField typeTextField;
    public TextField startDateTextField;
    public TextField startTimeTextField;
    public TextField endDateTextField;
    public TextField endTimeTextField;
    public TextField customerIdTextField;
    public TextField userIdTextField;
    public Text appointmentFormTitleText;
    public TextField idTextField;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public TextField locationTextField;
    public ComboBox<String> contactComboBox;
    public TextField updatedByTextField;
    public TextField updateTimeTextField;
    public TextField updateDateTextField;
    public TextField createdByTextField;
    public TextField createTimeTextField;
    public TextField createDateTextField;
    private Customer customer;
    private Appointment appointment;
    private final ContactDAO contactDAO = new ContactDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

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
    public void sendCustomerAndAppointment(Customer customer, Appointment appointment) throws SQLException {
        this.customer = customer;
        this.appointment = appointment;
//        fillForm();
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
            appointmentFormTitleText.setText("Modify " + customer.getName() + "'s Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            typeTextField.setText(appointment.getType());

            Timestamp timestampStart = appointment.getStart();
            Timestamp timestampEnd = appointment.getEnd();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(timestampStart);
            String dateEnd = dateFormat.format(timestampEnd);
            String timeStart = timeFormat.format(timestampStart);
            String timeEnd = timeFormat.format(timestampEnd);

            startDateTextField.setText(dateStart);
            startTimeTextField.setText(timeStart);
            endDateTextField.setText(dateEnd);
            endTimeTextField.setText(timeEnd);

            Timestamp createdTimestamp = appointment.getCreateDate();
            String createdDate = dateFormat.format(createdTimestamp);
            String createdTime = timeFormat.format(createdTimestamp);
            createDateTextField.setText(createdDate);
            createTimeTextField.setText(createdTime);

            createdByTextField.setText(appointment.getCreatedBy());

            Timestamp updatedTimestamp = appointment.getLastUpdate();
            String updatedDate = dateFormat.format(updatedTimestamp);
            String updatedTime = timeFormat.format(updatedTimestamp);

            updateDateTextField.setText(updatedDate);
            updateTimeTextField.setText(updatedTime);

            customerIdTextField.setText(Integer.toString(customer.getId()));
            customerIdTextField.setDisable(true);
            userIdTextField.setText(Integer.toString(appointment.getUserId()));
        }
        // adding appointment for specified customer
        else if (appointment == null && customer != null) {
            appointmentFormTitleText.setText("Add Appointment For " + customer.getName());
            idTextField.setText(Integer.toString(appointmentDAO.getNextId()));
            customerIdTextField.setText(Integer.toString(customer.getId()));
            customerIdTextField.setDisable(true);
        }
        // modifying an existing appointment
        else if (appointment != null) {
            appointmentFormTitleText.setText("Modify Appointment");
            idTextField.setText(Integer.toString(appointment.getId()));

            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            contactComboBox.getSelectionModel().select(contactDAO.getContactById(appointment.getContactId()).getName());
            typeTextField.setText(appointment.getType());

            Timestamp timestampStart = appointment.getStart();
            Timestamp timestampEnd = appointment.getEnd();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(timestampStart);
            String dateEnd = dateFormat.format(timestampEnd);
            String timeStart = timeFormat.format(timestampStart);
            String timeEnd = timeFormat.format(timestampEnd);

            startDateTextField.setText(dateStart);
            startTimeTextField.setText(timeStart);
            endDateTextField.setText(dateEnd);
            endTimeTextField.setText(timeEnd);

            Timestamp createdTimestamp = appointment.getCreateDate();
            String createdDate = dateFormat.format(createdTimestamp);
            String createdTime = timeFormat.format(createdTimestamp);
            createDateTextField.setText(createdDate);
            createTimeTextField.setText(createdTime);

            createdByTextField.setText(appointment.getCreatedBy());

            Timestamp updatedTimestamp = appointment.getLastUpdate();
            String updatedDate = dateFormat.format(updatedTimestamp);
            String updatedTime = timeFormat.format(updatedTimestamp);

            updateDateTextField.setText(updatedDate);
            updateTimeTextField.setText(updatedTime);

            updatedByTextField.setText(appointment.getLastUpdatedBy());

            customerIdTextField.setText(Integer.toString(appointment.getCustomerId()));
            userIdTextField.setText(Integer.toString(appointment.getUserId()));
        }
        // adding an appointment
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillForm();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onActionCancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void onActionSaveButton(ActionEvent actionEvent) throws SQLException {
        int id = Integer.parseInt(idTextField.getText());
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String type = typeTextField.getText();

        //create Start and End appointment time Timestamps
        String startDate = startDateTextField.getText();
        String startTime = startTimeTextField.getText();
        Timestamp start = TimeUtil.formValueToUTCTimestamp(startDate, startTime);
        String endDate = endDateTextField.getText();
        String endTime = endTimeTextField.getText();
        Timestamp end = TimeUtil.formValueToUTCTimestamp(endDate, endTime);

        //create Created start and end time Timestamps
        String createDate = createDateTextField.getText();
        String createTime = createTimeTextField.getText();
        Timestamp createTimestamp = TimeUtil.formValueToUTCTimestamp(createDate, createTime);

        //create Updated start and end time Timestamps
        String updateDate = updateDateTextField.getText();
        String updateTime = updateTimeTextField.getText();
        Timestamp updateTimestamp = TimeUtil.formValueToUTCTimestamp(updateDate, updateTime);

        String createdBy = createdByTextField.getText();
        String updatedBy = updatedByTextField.getText();
        int customerId = Integer.parseInt(customerIdTextField.getText());
        int userId = Integer.parseInt(userIdTextField.getText());
        int contactId = contactDAO.getContactIdByName(contactComboBox.getValue());

        Appointment newAppointment = new Appointment(id, title, description, location, type, start, end, createTimestamp, createdBy, updateTimestamp, updatedBy, customerId, userId, contactId);
        if (appointment == null) {
            appointmentDAO.addAppointment(newAppointment);
        } else {
            appointmentDAO.updateAppointment(newAppointment);
        }

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

    }
}

