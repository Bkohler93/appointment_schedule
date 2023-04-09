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

public class ContactScheduleController implements Initializable {
    private AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private ContactDAO contactDAO = new ContactDAOImpl();
    public ComboBox<String> contactComboBox;
    public TableView<Appointment> appointmentTableView;
    public TableColumn<Appointment, Integer> idCol;
    public TableColumn<Appointment, String> titleCol;
    public TableColumn<Appointment, String> typeCol;
    public TableColumn<Appointment, String> descriptionCol;
    public TableColumn<Appointment, String> startCol;
    public TableColumn<Appointment, String> endCol;
    public TableColumn<Appointment, Integer> customerIdCol;

    public void onActionContactComboBox(ActionEvent actionEvent) throws Exception {
        try {
           String contactName = contactComboBox.getValue();
           Contact selectedContact = contactDAO.getContactById(contactDAO.getContactIdByName(contactName));
           appointmentTableView.setItems(appointmentDAO.getAllContactAppointments(selectedContact));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void onActionBackButton(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
