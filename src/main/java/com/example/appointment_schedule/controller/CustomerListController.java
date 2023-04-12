package com.example.appointment_schedule.controller;


import com.example.appointment_schedule.Main;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.customer.CustomerDAO;
import com.example.appointment_schedule.dao.customer.CustomerDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Customer;
import com.example.appointment_schedule.util.FxUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.example.appointment_schedule.util.FxUtil.clearInfoDisplayText;
import static com.example.appointment_schedule.util.FxUtil.displayInfoDisplayText;

public class CustomerListController implements Initializable {
    private final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> customerNameCol;
    @FXML
    private TableColumn<Customer, String> customerAddressCol;
    @FXML
    private TableColumn<Customer, String> customerPostalCol;
    @FXML
    private TableColumn<Customer, String> customerPhoneCol;
    @FXML
    private TableColumn<Customer, Integer> customerIdCol;
    @FXML
    private TableColumn<Customer, LocalDateTime> customerCreateDateCol;
    @FXML
    private TableColumn<Customer, String> customerCreatedByCol;
    @FXML
    private TableColumn<Customer, LocalDateTime> customerLastUpdateCol;
    @FXML
    private TableColumn<Customer, String> customerLastUpdatedByCol;
    @FXML
    private TableColumn<Customer, Integer> customerDivisionIdCol;

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
    private Text customerAppointmentsTitleText;
    @FXML
    private Text infoDisplayText;

    /** LAMBDA EXPRESSIONS HERE!
     * initializes table values to be filled with customers and appointments.
     * @param url not used
     * @param resourceBundle not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // fill customerTableView with all customers from customerDAO
        try {
            customerTableView.setItems(customerDAO.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        clearInfoDisplayText(infoDisplayText);

        setUpTableColumnValues();

        // Selecting a row (customer) in the customerTableView focuses the row, this lambda is triggered when that happens
        // and causes the appointmentTableView to be populated with the currently selected customer.
        customerTableView.setOnMouseClicked(event -> {
            clearInfoDisplayText(infoDisplayText);
            Customer customer = customerTableView.getSelectionModel().getSelectedItem();
            if (customer != null) {
                try {
                    customerAppointmentsTitleText.setText(customer.getName() + "'s Appointments Quick View");
                    appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
                    if (appointmentTableView.getItems().isEmpty()) {
                        Label emptyPlaceholderLabel = new Label(customer.getName() + " has no appointments scheduled.");
                        appointmentTableView.setPlaceholder(emptyPlaceholderLabel);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Up/Down arrows move the customerTableView selection up and down. This
        // fills in the appointment table with the currently selected customer.
        customerTableView.setOnKeyPressed(event -> {
            clearInfoDisplayText(infoDisplayText);
            Customer customer = customerTableView.getSelectionModel().getSelectedItem();
            if (customer != null) {
                try {
                    customerAppointmentsTitleText.setText(customer.getName() + "'s Appointments Quick View");
                    appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
                    if (appointmentTableView.getItems().isEmpty()) {
                        Label emptyPlaceholderLabel = new Label(customer.getName() + " has no appointments scheduled.");
                        appointmentTableView.setPlaceholder(emptyPlaceholderLabel);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    /**
     * Helper method to set up customerTableView and appointmentTableView columns with their respective data
     * from their Model classes
     */
    private void setUpTableColumnValues() {
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerCreateDateCol.setCellValueFactory(new PropertyValueFactory<>("createDate"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerCreatedByCol.setCellValueFactory(new PropertyValueFactory<>("createdBy"));
        customerLastUpdateCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdate"));
        customerDivisionIdCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
        customerLastUpdatedByCol.setCellValueFactory(new PropertyValueFactory<>("lastUpdatedBy"));

        appointmentEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        appointmentStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        Label placeholderLabel = new Label("Select a customer to view appointments.");
        appointmentTableView.setPlaceholder(placeholderLabel);
    }

    /**
     * closes window and returns to previous form
     * @param actionEvent event propagated after clicking on Back button
     */
    @FXML
    public void closeWindow(ActionEvent actionEvent) {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /**
     * navigate to Customer.fxml to add a new customer.
     * @param actionEvent event propagated after clicking on Add button
     * @throws IOException thrown if Customer.fxml is not found in `resources` directory
     */
    @FXML
    public void navigateToCustomerFormToAddCustomer(ActionEvent actionEvent) throws IOException {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateTo("Customer.fxml", stage, this::loadCustomers);
    }

    /**
     * navigate to Customer.fxml to modify the currently selected customer (displays error if no customer
     * is selected).
     * @param actionEvent event propagated after clicking on Modify button
     * @throws IOException thrown if Customer.fxml is not found in `resources` directory
     * @throws SQLException thrown if invalid customer is sent to CustomerController
     */
    @FXML
    public void navigateToCustomerFormToModifyCustomer(ActionEvent actionEvent) throws IOException, SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (customer != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Customer.fxml"));
            loader.load();

            CustomerController customerController = loader.getController();
            customerController.sendCustomer(customer);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, this::loadCustomers);
        } else {
            displayInfoDisplayText("Select a customer to modify.", true, infoDisplayText);
        }
    }

    /**
     * deletes currently selected customer from database. Updates customerTableView with removed customer. Displays error
     * if no customer is selected.
     * @throws SQLException thrown if invalid customer is attempted to be deleted
     */
    @FXML
    public void deleteCustomer() throws SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        boolean hasAppointments = !appointmentTableView.getItems().isEmpty();
        if (hasAppointments) {
            displayInfoDisplayText(customer.getName() + " has scheduled appointments and cannot be deleted.", true, infoDisplayText);
        } else if (customer != null) {
            customerDAO.deleteCustomer(customer);
            customerTableView.setItems(customerDAO.getAllCustomers());
            displayInfoDisplayText("Deleted " + customer.getName() + ".", false, infoDisplayText);
        } else {
            displayInfoDisplayText("Select a customer to delete.", true, infoDisplayText);
        }
    }

    /**
     * navigates to Appointment.fxml to add an appointment for currently selected customer. If no customer is selected
     * an error message appears in the UI.
     * @param actionEvent event propagated from clicking on `Add` button underneath AppointmentTableView
     * @throws IOException thrown if Appointment.fxml is not found in `resources` directory
     * @throws SQLException thrown if invalid customer is attempted to be deleted.
     */
    public void navigateToAppointmentFormToAddAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (customer != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.sendCustomer(customer);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, this::loadAppointments);
        } else {
            displayInfoDisplayText("Select a customer to add an appointment for.", true, infoDisplayText);
        }
    }

    /**
     * navigates to Appointment.fxml to modify currently selected appointment for currently selected customer. Displays
     * an error if no appointment is selected.
     * @param actionEvent event propagated from clicking on `Modify` button underneath the appointmentTableView.
     * @throws IOException thrown if Appointment.fxml is not found in `resources` directory
     */
    @FXML
    public void navigateToAppointmentFormToModifyAppointment(ActionEvent actionEvent) throws IOException {
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.sendCustomerAndAppointment(customer, appointment);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, this::loadAppointments);
        } else {
            displayInfoDisplayText("Select an appointment to modify.", true, infoDisplayText);
        }
    }

    /**
     * deletes selected customer's appointment if an appointment is selected. Displays an error if no appointment is selected.
     * @throws SQLException thrown if invalid appointment is attempted to be deleted.
     */
    public void deleteAppointment() throws SQLException {
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            appointmentDAO.deleteAppointment(appointment);
            appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
            displayInfoDisplayText("'" + appointment.getTitle() + "' appointment has been deleted. (Appointment_ID = " + appointment.getId() + ").", false, infoDisplayText);
        } else {
            displayInfoDisplayText("Select an appointment to delete.", true, infoDisplayText);
        }
    }

    /**
     * helper method to reload customer list after performing an Add/Modify/Delete function. Public to be used in other controllers
     * that need to reload the list of customers.
     */
    public void loadCustomers() {
        try {
            customerTableView.setItems(customerDAO.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * helper method to reload appointment list after modifying/deleting/adding appointments.
     * @throws SQLException thrown by appointmentDAO
     */
    private void loadAppointments() throws SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
       appointmentDAO.getAllAppointments();
       appointmentTableView.setItems(appointmentDAO.getAllCustomerAppointments(customer));
    }
}
