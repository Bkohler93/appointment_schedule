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
import javafx.scene.paint.Color;
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
    public TableView<Customer> customerTableView;
    @FXML
    public TableColumn<Customer, String> customerNameCol;
    @FXML
    public TableColumn<Customer, String> customerAddressCol;
    @FXML
    public TableColumn<Customer, String> customerPostalCol;
    @FXML
    public TableColumn<Customer, String> customerPhoneCol;
    @FXML
    public TableColumn<Customer, Integer> customerIdCol;
    @FXML
    public TableColumn<Customer, LocalDateTime> customerCreateDateCol;
    @FXML
    public TableColumn<Customer, String> customerCreatedByCol;
    @FXML
    public TableColumn<Customer, LocalDateTime> customerLastUpdateCol;
    @FXML
    public TableColumn<Customer, String> customerLastUpdatedByCol;
    @FXML
    public TableColumn<Customer, Integer> customerDivisionIdCol;

    @FXML
    public TableView<Appointment> appointmentTableView;
    @FXML
    public TableColumn<Appointment, Integer> appointmentIdCol;
    @FXML
    public TableColumn<Appointment, String> appointmentTitleCol;
    @FXML
    public TableColumn<Appointment, LocalDateTime> appointmentStartCol;
    @FXML
    public TableColumn<Appointment, LocalDateTime> appointmentEndCol;
    @FXML
    private Text customerAppointmentsTitleText;
    @FXML
    private Text infoDisplayText;

    /**
     * @param url TODO
     * @param resourceBundle TODO
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            customerTableView.setItems(customerDAO.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        infoDisplayText.setFill(Color.RED);
        clearInfoDisplayText(infoDisplayText);
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
        Label placeholderLabel = new Label("Select a customer to main.com.appointment_tracker.view appointments.");
        appointmentTableView.setPlaceholder(placeholderLabel);

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

    // returns to previous form
    @FXML
    public void onActionBackButton(ActionEvent actionEvent) {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    // navigates to Customer.fxml
    @FXML
    public void onActionAddCustomerButton(ActionEvent actionEvent) throws IOException {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateTo("Customer.fxml", stage, null);
    }

    // navigates to Customer.fxml only if a customer is selected from customerTableView. Sends that customer
    // to Customer.fxml's controller
    @FXML
    public void onActionModifyCustomerButton(ActionEvent actionEvent) throws IOException, SQLException {
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

    // deletes customer if they have zero scheduled appointments.
    @FXML
    public void onActionDeleteCustomerButton(ActionEvent actionEvent) throws SQLException {
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

    // navigates to Appointment.fxml only if a customer is selected from customerTableView.
    // Sends the selected customer to Appointment.fxml's controller
    public void onActionAddAppointmentButton(ActionEvent actionEvent) throws IOException, SQLException {
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (customer != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.sendCustomer(customer);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, null);
        } else {
            displayInfoDisplayText("Select a customer to add an appointment for.", true, infoDisplayText);
        }
    }

    // navigates to Appointment.fxml only if an appointment is selected in appointmentTableView
    public void onActionModifyAppointmentButton(ActionEvent actionEvent) throws IOException, SQLException {
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        Customer customer = customerTableView.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.sendCustomerAndAppointment(customer, appointment);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, null);
        } else {
            displayInfoDisplayText("Select an appointment to modify.", true, infoDisplayText);
        }
    }

    public void onActionDeleteAppointmentButton(ActionEvent actionEvent) throws SQLException {
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

    public void loadCustomers() {
        try {
            customerTableView.setItems(customerDAO.getAllCustomers());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    private void clearInfoDisplayText() {
//        infoDisplayText.setText("");
//    }

//    private void displayInfoDisplayText(String info, boolean isError) {
//        if (isError) {
//            infoDisplayText.setFill(Color.RED);
//        } else {
//            infoDisplayText.setFill(Color.GREEN);
//        }
//        infoDisplayText.setText(info);
//    }
}
