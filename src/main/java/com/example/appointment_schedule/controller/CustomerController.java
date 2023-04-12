package com.example.appointment_schedule.controller;


import com.example.appointment_schedule.Main;
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
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * provides functionality to the Customer.fxml form
 * @author Brett Kohler`
 */
public class
CustomerController implements Initializable {

    private Customer customer;
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final CountryDAO countryDAO = new CountryDAOImpl();
    private final FirstLevelDivisionDAO firstLevelDivisionDAO = new FirstLevelDivisionDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
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


    /**
     * Sends a customer from another form's controller to this controller. Allows other forms,
     * specifically `ContactScheduleController` to navigate to the Customer.fxml while sending a customer.
     * @param customer customer to send
     * @throws SQLException exception thrown by invalid arguments in DAO methods
     */
    public void sendCustomer(Customer customer) throws SQLException {
        // save customer data and retrieve customer's data to display on form fields
        this.customer = customer;
        FirstLevelDivision division = firstLevelDivisionDAO.getFirstLevelDivisionById(customer.getDivisionId());
        Country country = countryDAO.getCountryById(division.getCountryId());

        // fill form fields with customer data
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

        // set column values in TableView
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
    }

    /**
     * in order to add an appointment for the customer currently being viewed, the customer must be sent
     * to the  AppointmentController in order to populate Appointment.fxml with the correct customerID.
     * @param actionEvent event propagated after clicking on `Add` button under Appointment TableView
     */
    @FXML
    public void navigateToAppointmentFormToAddAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        // load CustomerList form from `resources` directory
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("Appointment.fxml"));
        loader.load();

        // load controller to set up refreshing of customer list when returning to form
        AppointmentController appointmentController = loader.getController();
        appointmentController.sendCustomer(customer);

        // access the stage to use within `navigateToWithData`
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateToWithData(stage, loader, this::resetAppointmentSchedule);
    }

    /**
     * each time the program returns to Customer.fxml the AppointmentTableView should be updated
     * with the customer's updated appointments
     * @throws SQLException thrown by an invalid customer being sent to appointmentDAO
     */
    private void resetAppointmentSchedule() throws SQLException {
        appointmentDAO.getAllAppointments();
        appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
    }

    /**
     * in order to modify the currently selected appointment, the AppointmentController for Appointment.fxml
     * must be loaded in order to send the appointment and customer to the controller in order to properly
     * fill the form fields in Appointment.fxml
     * @param actionEvent event propagated after clicking on Modify button
     * @throws IOException thrown if Appointment.fxml is not found in the `resources` directory
     */
    @FXML
    public void navigateToAppointmentFormToModifyAppointment(ActionEvent actionEvent) throws IOException {
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        // load CustomerList form from `resources` directory
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("Appointment.fxml"));
        loader.load();

        // load controller to set up refreshing of customer list when returning to form
        AppointmentController appointmentController = loader.getController();
        appointmentController.sendCustomerAndAppointment(customer, selectedAppointment);

        // access the stage to use within `navigateToWithData`
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateToWithData(stage, loader, this::resetAppointmentSchedule);
    }

    /**
     * select the currently selected appointment from AppointmentTableView and use the appointmentDAO to
     * delete the appointment.
     * @throws SQLException thrown if invalid appointment is sent to appointmentDAO to be deleted
     */
    @FXML
    public void deleteSelectedAppointment() throws SQLException {
        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
        appointmentDAO.deleteAppointment(selectedAppointment);
        resetAppointmentSchedule();
    }

    /**
     * use current stage to fire a close request on form
     * @param actionEvent event propagated from clicking on Cancel button
     */
    @FXML
    public void navigateToPreviousForm(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * retrieve all values from TextFields in order to create a new Customer objet. Depending on whether
     * the form is in Modify or Add mode, the correct DAO method will be executed to save the customer data.
     * @param actionEvent event propagated after clicking on Save button
     * @throws SQLException thrown if invalid Customer is being sent to customerDAO
     */
    @FXML
    public void saveUpdatedCustomer(ActionEvent actionEvent) throws SQLException {

        // retrieve field values from form
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

        // create new customer object to save/update
        Customer customerToSave = new Customer(id, name, address, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdatedBy, divisionId);

        if (customer == null) {
            customerDAO.addCustomer(customerToSave);
        } else {
            customerDAO.updateCustomer(customerToSave);
        }

        // close form, returning to previous form
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * LAMBDA EXPRESSION HERE!
     * fills in form fields that hold unmodifiable data. Retrieves data using customerDAO/countryDAO.
     * @param url not used
     * @param resourceBundle not used
     */
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

