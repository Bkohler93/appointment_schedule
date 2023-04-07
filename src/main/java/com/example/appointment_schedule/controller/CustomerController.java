package com.example.appointment_schedule.controller;


import com.example.appointment_schedule.auth.Auth;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.country.CountryDAO;
import com.example.appointment_schedule.dao.country.CountryDAOImpl;
import com.example.appointment_schedule.dao.customer.CustomerDAO;
import com.example.appointment_schedule.dao.customer.CustomerDAOImpl;
import com.example.appointment_schedule.dao.firstLevelDivision.FirstLevelDivisionDAO;
import com.example.appointment_schedule.dao.firstLevelDivision.FirstLevelDivisionDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Country;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.model.FirstLevelDivision;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class
CustomerController implements Initializable {

    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleCol;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartCol;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndCol;

    @FXML
    private Line appointmentLineBreak;
    @FXML
    private VBox appointmentVBox;
    @FXML
    private ComboBox<String> customerCountryComboBox;
    @FXML
    private ComboBox<String> customerFirstLevelComboBox;
    @FXML
    private Text customerTitleText;
    @FXML
    private TextField customerIdTextField;
    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField customerAddressTextField;
    @FXML
    private TextField customerPostalCodeTextField;
    @FXML
    private TextField customerPhoneTextField;
    private Customer customer;
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final CountryDAO countryDAO = new CountryDAOImpl();
    private final FirstLevelDivisionDAO firstLevelDivisionDAO = new FirstLevelDivisionDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    public void sendCustomer(Customer customer) throws SQLException {
        this.customer = customer;
        FirstLevelDivision division = firstLevelDivisionDAO.getFirstLevelDivisionById(customer.getDivisionId());
        Country country = countryDAO.getCountryById(division.getCountryId());

        customerNameTextField.setText(customer.getName());
        customerIdTextField.setText(Integer.toString(customer.getId()));
        customerAddressTextField.setText(customer.getAddress());
        customerPostalCodeTextField.setText(customer.getPostalCode());
        customerPhoneTextField.setText(customer.getPhone());
        customerTitleText.setText("Modify Customer");
        customerCountryComboBox.getSelectionModel().select(country.getName());
        customerFirstLevelComboBox.getSelectionModel().select(division.getName());
        appointmentVBox.setVisible(true);
        appointmentLineBreak.setVisible(true);

        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));

    }

    public void onActionAddAppointmentButton(ActionEvent actionEvent) {
    }

    public void onActionModifyAppointmentButton(ActionEvent actionEvent) {
    }

    public void onActionDeleteAppointmentButton(ActionEvent actionEvent) {
    }

    public void onActionCancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public void onActionSaveButton(ActionEvent actionEvent) throws SQLException {
        int id = Integer.parseInt(customerIdTextField.getText());
        String name = customerNameTextField.getText();
        String address = customerAddressTextField.getText();
        String postalCode = customerPostalCodeTextField.getText();
        String phone = customerPhoneTextField.getText();
        Timestamp createDate = customer == null ? TimeUtil.UTCTimestampNow() : customer.getCreateDate();
        String createdBy = customer == null ? Auth.user.getUserName() : customer.getCreatedBy();
        Timestamp lastUpdate = TimeUtil.UTCTimestampNow();
        String lastUpdatedBy = Auth.user.getUserName();
        int divisionId = firstLevelDivisionDAO.getFirstLevelDivisionByName(customerFirstLevelComboBox.getSelectionModel().getSelectedItem()).getId();
        Customer customerToSave = new Customer(id, name, address, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionId);
        if (customer == null) {
            customerDAO.addCustomer(customerToSave);
        } else {
            customerDAO.updateCustomer(customerToSave);
        }
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        customerIdTextField.setDisable(true);
        appointmentVBox.setVisible(false);
        appointmentLineBreak.setVisible(false);
        try {
            customerIdTextField.setText(Integer.toString(customerDAO.getNextId()));
            customerCountryComboBox.setItems(countryDAO.getAllCountries().stream().map(Country::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // when a country is selected in this ComboBox, the FirstLevelComboBox gets filled with
        // the FirstLevelDivision name
        customerCountryComboBox.setOnAction(e -> {
            String selectedCountryName = customerCountryComboBox.getSelectionModel().getSelectedItem();
            try {
                Country selectedCountry = countryDAO.getCountryByName(selectedCountryName);
                customerFirstLevelComboBox.setValue(null);

                // sets items to the Division Names that match the selectedCountry's Country_ID
                customerFirstLevelComboBox.setItems(firstLevelDivisionDAO.getAllFirstLevelDivisions().stream().filter(d -> d.getCountryId() == selectedCountry.getId()).map(FirstLevelDivision::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
    }
}

