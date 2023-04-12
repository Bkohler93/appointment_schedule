package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Contact;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * provides functionality to the ContactSchedule.fxml form.
 * @author Brett Kohler
 */
public class ContactScheduleController implements Initializable {
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final ContactDAO contactDAO = new ContactDAOImpl();
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> idCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;

    /**
     * fills the TableView of appointments with contact's appointments after selecting a contact
     * from the Contact ComboBox (represented as a String). The retrieved contact includes
     * the contactId field, allowing the retrieval of appointments with contactId's that match the selected Contact.
     * @throws SQLException exception thrown by ContactDAO or AppointmentDAO from invalid contact being selected.
     */
    public void fillAppointmentTableBySelectedContact() throws SQLException {
           String contactName = contactComboBox.getValue();
           Contact selectedContact = contactDAO.getContactByName(contactName);
           appointmentTableView.setItems(appointmentDAO.getAllContactAppointments(selectedContact));
    }

    /**
     * Closes the ContactSchedule window to send user back to the main Appointment list form.
     * @param actionEvent event propagated from clicking on the Back button.
     */
    public void closeWindow(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * initializes columns in Appointments TableView with correct class attributes
     * @param url not used, required by super's `initialize` method
     * @param resourceBundle not used, required by super's `initialize` method
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // set up TableView columns with correct class properties
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        startCol.setCellValueFactory(cellData -> {
            Appointment appointment = cellData.getValue();
            try {
                return new ReadOnlyStringWrapper(TimeUtil.TimestampToTableValue(appointment.getStart()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        endCol.setCellValueFactory(cellData -> {
            Appointment appointment = cellData.getValue();
            try {
                return new ReadOnlyStringWrapper(TimeUtil.TimestampToTableValue(appointment.getEnd()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        try {
            contactComboBox.setItems(contactDAO.getAllContacts().stream().map(Contact::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
