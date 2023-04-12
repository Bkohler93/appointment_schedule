package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.Constants;
import com.example.appointment_schedule.Main;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.dao.country.CountryDAO;
import com.example.appointment_schedule.dao.country.CountryDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.Country;
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.example.appointment_schedule.util.FxUtil.clearInfoDisplayText;
import static com.example.appointment_schedule.util.FxUtil.displayInfoDisplayText;

/**
 * adds functionality to AppointmentSchedule.fxml
 * @author Brett Kohler
 */
public class AppointmentScheduleController implements Initializable {
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final ContactDAO contactDAO = new ContactDAOImpl();
    private final CountryDAO countryDAO = new CountryDAOImpl();
    private int selectedMonth;
    private int selectedYear;
    private String selectedWeekStartDate;
    @FXML
    private Button adjustAppointmentSaveButton;
    @FXML
    private Text upcomingAptNotificationText;
    @FXML
    private ComboBox<String> typeReportComboBox;
    @FXML
    private Text typeReportText;
    @FXML
    private Text byMonthReportText;
    @FXML
    private ComboBox<String> byMonthReportMonthComboBox;
    @FXML
    private Spinner<Integer> byMonthReportYearSpinner;
    @FXML
    private ComboBox<String> byCountryReportComboBox;
    @FXML
    private Text byCountryReportText;
    @FXML
    private Button cancelButton;
    @FXML
    private Text infoDisplayText;
    @FXML
    private TextField dateStartInputTextField;
    @FXML
    private TextField timeStartInputTextField;
    @FXML
    private TextField dateEndInputTextField;
    @FXML
    private TextField timeEndInputTextField;
    @FXML
    private Label selectedTimeLabel;
    @FXML
    private RadioButton byMonthRadioButton;
    @FXML
    private RadioButton byWeekRadioButton;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, Integer> idCol;
    @FXML
    private TableColumn<Appointment, String> titleCol;
    @FXML
    private TableColumn<Appointment, String> descriptionCol;
    @FXML
    private TableColumn<Appointment, String> locationCol;
    @FXML
    private TableColumn<Appointment, String> contactCol;
    @FXML
    private TableColumn<Appointment, String> typeCol;
    @FXML
    private TableColumn<Appointment, String> startCol;
    @FXML
    private TableColumn<Appointment, String> endCol;
    @FXML
    private TableColumn<Appointment, Integer> customerIdCol;
    @FXML
    private TableColumn<Appointment, Integer> userIdCol;

    /**
     * navigate to CustomerList.fxml form
     * @param actionEvent event propagated from user clicking on Customers button
     * @throws IOException thrown if invalid URL for .fxml form
     */
    public void navigateToCustomerList(ActionEvent actionEvent) throws IOException {
        
        // load CustomerList form from `resources` directory
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("CustomerList.fxml"));
        loader.load();

        // load controller to set up refreshing of customer list when returning to form
        CustomerListController customerListController = loader.getController();

        // access the stage to use within `navigateToWithData`
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateToWithData(stage, loader, customerListController::loadCustomers);
    }

    /**
     * initializes AppointmentSchedule.fxml with form fields.
     * @param url NA
     * @param resourceBundle NA
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpTableValues();
        checkForUpcomingAppointments();
        fillFormFields();
        fillAppointmentTableView();
    }

    /**
     * sets up form fields with correct values and sets up component functionality
     */
    private void fillFormFields() {
        // assign correct status for buttons so that user cannot attempt to cancel an appointment
        // when none is selected and the application starts by viewing appointments by month
        byMonthRadioButton.setSelected(true);
        cancelButton.setDisable(true);

        // get current Month and Year to start by viewing appointments by Month/Year
        LocalDate now = LocalDate.now();
        selectedMonth = now.getMonth().getValue();
        String selectedMonthStr = Month.of(selectedMonth).name();
        selectedYear = now.getYear();
        selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

        setUpFormOnClickAndOnKey();

        // fill ComboBoxes
        typeReportComboBox.setItems(appointmentDAO.getUniqueTypeNames());
        byMonthReportMonthComboBox.setItems(Constants.months);
        try {
            byCountryReportComboBox.setItems(countryDAO.getAllCountries().stream().map(Country::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // initialize report text with empty values
        typeReportText.setText("");
        byMonthReportText.setText("");

        // set up By Month/Year report spinner values to operate on up/down arrows correctly
        int year = TimeUtil.getYear();
        byMonthReportYearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(year, year + 50, year));
        byMonthReportYearSpinner.increment(1);
        byMonthReportYearSpinner.decrement(1);
    }

    /**
     * appointmentDAO is first accessed by this page, so all appointments need to be retrieved by the appointmentDAO.
     * Begins by viewing appointments by month/year, so these appointments are retrieved.
     */
    private void fillAppointmentTableView() {
        try {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * When a user selects a appointment from the TableView by clicking on a table cell / using arrow keys
     * the corresponding appointment is selected. When a user types in any of the Time fields for adjusting
     * an appointment, a method is triggered to display correct status based on validity of each field.
     */
    private void setUpFormOnClickAndOnKey() {
        appointmentTableView.setOnMouseClicked(event ->
           selectAppointmentFromTableView());
        appointmentTableView.setOnKeyPressed(event ->
            selectAppointmentFromTableView());

        // handle keystrokes when user types in the TextFields for adjusting appointment time
        timeStartInputTextField.setOnKeyTyped(keyEvent -> checkForValidAdjustmentDateTimes());
        timeEndInputTextField.setOnKeyTyped(keyEvent ->  checkForValidAdjustmentDateTimes());
        dateStartInputTextField.setOnKeyTyped(keyEvent -> checkForValidAdjustmentDateTimes());
        dateEndInputTextField.setOnKeyTyped(keyEvent ->  checkForValidAdjustmentDateTimes());
    }

    /**
     * retrieves any appointments scheduled within 15 minutes of login time. If no appointments are scheduled,
     * the UI displays a notification stating no 'no upcoming appointments'. If there is an appointment, the UI
     * displays appointment information.
     */
    private void checkForUpcomingAppointments() {
        Appointment upcomingAppointment;
        try {
            upcomingAppointment = appointmentDAO.getUpcomingAppointment();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (upcomingAppointment == null) {
            upcomingAptNotificationText.setText("No upcoming appointments");
        } else {
            Timestamp start = upcomingAppointment.getStart();
            int id = upcomingAppointment.getId();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(start);
            String timeStart = timeFormat.format(start);
            upcomingAptNotificationText.setText("Upcoming appointment[" + id + "] at " + dateStart + " " + timeStart);
        }
    }

    /** LAMBDA EXPRESSION HERE!
     * TableView cells must match the Appointment model class attributes. Appointment model includes a contactId and no
     * contact name, so that column includes a lambda expression to convert the contactId into the contactName
     * by using the contactDAO to retrieve the contact based on contactId from the Appointment model. Time columns
     * also include a lambda to format the timestamps into a more readable format.
     */
    private void setUpTableValues() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(cellData -> {
            Appointment appointment = cellData.getValue();
            try {
                return new ReadOnlyStringWrapper(contactDAO.getContactById(appointment.getContactId()).getName());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
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
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
    }

    /**
     * selects an appointment from the AppointmentTableView and fills in adjustment times with appointment start/end times
     */
    private void selectAppointmentFromTableView() {
        // update form actions based on currently selected appointment
        adjustAppointmentSaveButton.setDisable(true);
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(false);

        // select the appointment
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();

        // fill in adjustment times if appointment is selected
        if (appointment != null) {

            // retrieve and format times
            Timestamp timestampStart = appointment.getStart();
            Timestamp timestampEnd = appointment.getEnd();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            String dateStart = dateFormat.format(timestampStart);
            String dateEnd = dateFormat.format(timestampEnd);
            String timeStart = timeFormat.format(timestampStart);
            String timeEnd = timeFormat.format(timestampEnd);

            // fill in adjustment TextFields with formatted times
            dateStartInputTextField.setText(dateStart);
            timeStartInputTextField.setText(timeStart);
            dateEndInputTextField.setText(dateEnd);
            timeEndInputTextField.setText(timeEnd);
        }
    }

    /**
     * determines if currently entered text in appointment adjustment fields are valid and enables the save button
     * if they are valid, or disables it if any of the fields are invalid.
     */
    private void checkForValidAdjustmentDateTimes() {

        //clear any display text in InfoDisplay Text
        clearInfoDisplayText(infoDisplayText);

        // select the currently selected appointment
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) { return; }

        // retrieve text from TextFields
        String dateStart = dateStartInputTextField.getText();
        String timeStart = timeStartInputTextField.getText();
        String dateEnd = dateEndInputTextField.getText();
        String timeEnd = timeEndInputTextField.getText();

        if (!(TimeUtil.isValidDate(dateStart) && TimeUtil.isValidTime(timeStart) && TimeUtil.isValidDate(dateEnd) && TimeUtil.isValidTime(timeEnd))) {
            adjustAppointmentSaveButton.setDisable(true);
            return;
        }

        // create timestamps from valid time values
        Timestamp timestampStart = TimeUtil.formValueToTimestamp(dateStart, timeStart);
        Timestamp timestampEnd = TimeUtil.formValueToTimestamp(dateEnd, timeEnd);

        // disable button if all adjust date/times are same as currently selected appointment.
        adjustAppointmentSaveButton.setDisable(timestampStart.equals(appointment.getStart()) && timestampEnd.equals(appointment.getEnd()));
    }

    /**
     * Causes the AppointmentTableView to be updated  after selecting the previous button (if currently displaying appointments
     * for March 2020, this method will display appointments for February 2020).
     */
    @FXML
    public void fillAppointmentsBasedOnPreviousTimeInterval() {
        // clear info display and input text fields
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);

        // fills in table data with by Month data or by Week data
        if (byMonthRadioButton.isSelected()) {

            // updates selectedYear/Month based on if currently at start of year
            selectedYear = selectedMonth == 1 ? selectedYear - 1 : selectedYear;
            selectedMonth = selectedMonth == 1 ? 12 : selectedMonth - 1;
            String selectedMonthStr = Month.of(selectedMonth).name();
            selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

            // fill in Table with appointments by month/year
            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // update selected week based on previously selected week
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(selectedWeekStartDate, formatter);
            LocalDate nextWeek = date.plusWeeks(-1);
            selectedWeekStartDate = nextWeek.format(formatter);
            selectedTimeLabel.setText("Week of " + selectedWeekStartDate);

            // fill in Table with appointments by week
            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Causes the Appointment TableView to be updated after selecting the next button (if displaying March 2020 appointments,
     * this button would cause April 2020 appointments to be selected).
     */
    @FXML
    public void fillAppointmentsBasedOnNextTimeInterval() {
        // clear info display and text fields
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);

        // fills in table data based on by month or by week data
        if (byMonthRadioButton.isSelected()) {

            // updates selected year/month on whether at end of year or not.
            selectedYear = selectedMonth == 12 ? selectedYear + 1 : selectedYear;
            selectedMonth = selectedMonth == 12 ? 1 : selectedMonth + 1;
            String selectedMonthStr = Month.of(selectedMonth).name();
            selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

            // fill in table with appointments
            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // updates selected week based on previously selected week
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(selectedWeekStartDate, formatter);
            LocalDate nextWeek = date.plusWeeks(1);
            selectedWeekStartDate = nextWeek.format(formatter);
            selectedTimeLabel.setText("Week of " + selectedWeekStartDate);

            // fill in table with appointments
            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * changes the table view to view appointments by month
     */
    @FXML
    public void fillAppointmentsByMonthYear() {

        // clear errors and update UI to reflect By Month view
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);
        byWeekRadioButton.setSelected(false);
        byMonthRadioButton.setSelected(true);
        String selectedMonthStr = Month.of(selectedMonth).name();
        selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

        // fill table with appointments
        try {
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * changes the table view to view appointments by week
     * @throws SQLException thrown by appointmentDAO when retrieving appointments byt week
     */
    @FXML
    public void fillAppointmentsByWeek() throws SQLException {

        //update UI for viewing appointments by week
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);
        byMonthRadioButton.setSelected(false);
        byWeekRadioButton.setSelected(true);
        if (selectedWeekStartDate == null) {
            LocalDateTime now = LocalDateTime.now();
            selectedWeekStartDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        selectedTimeLabel.setText("Week of " + selectedWeekStartDate);

        // fill table with appointments by week
        appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
    }

    /**
     * clears Time entry text fields
     */
    private void clearDateTimeInputTextFields() {
        timeStartInputTextField.setText("");
        timeEndInputTextField.setText("");
        dateStartInputTextField.setText("");
        dateEndInputTextField.setText("");
    }

    /**
     * navigates to Appointment.fxml form with currently selected appointment (or displays error to choose an appointmentto modify)
     * @param actionEvent event propagated after clicking on Modify button
     * @throws IOException thrown when Appointment.fxml is not located in Resources directory
     * @throws SQLException thrown by sending an invalid appointment to appointmentController
     */
    @FXML
    public void navigateToAppointmentFormToModifyAppointment(ActionEvent actionEvent) throws IOException, SQLException {
        // get selected appointment
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();

        // clear any errors
        clearInfoDisplayText(infoDisplayText);

        // check if appointment is selected or not
        if (appointment != null) {

            // load Appointment.fxml and controller
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();
            AppointmentController appointmentController = loader.getController();

            // send selected appointment to appointmentController
            appointmentController.sendAppointment(appointment);

            // navigate to new form, set to reload appointments on returning
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, this::resetAppointmentScheduleByMonth);
        } else {
            displayInfoDisplayText("Select an appointment to modify.", true, infoDisplayText);
        }
    }

    /**
     * reloads appointments by month (the default view when AppointmentSchedule.fxml form is first opened)
     */
    public void resetAppointmentScheduleByMonth() {
        try {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
            typeReportComboBox.setItems(appointmentDAO.getUniqueTypeNames());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * navigates to Appointment.fxml form with no selected appointment.
     * @param actionEvent propagated after clicking on Add button
     * @throws IOException thrown if Appointment.fxml is not inside Resources directory
     */
    @FXML
    public void navigateToAppointmentFormToAddAppointment(ActionEvent actionEvent) throws IOException {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateTo("Appointment.fxml", stage, this::resetAppointmentScheduleByMonth);
    }

    /**
     * Cancels selected appointment and display a notification about which appointment was deleted
     * @throws SQLException thrown if invalid appointment to delete is sent to appointmentDAO
     */
    @FXML
    public void cancelSelectedAppointment() throws SQLException {
        Appointment appointmentToCancel = appointmentTableView.getSelectionModel().getSelectedItem();
        appointmentDAO.deleteAppointment(appointmentToCancel);
        displayInfoDisplayText("Appointment ID[" + appointmentToCancel.getId() + "]: " + appointmentToCancel.getType() + " cancelled", false, infoDisplayText);
    }

    /**
     * Saves currently entered time values as new appointment start/end times
     * @throws SQLException  thrown by appointmentDAO, multiple methods used.
     */
    @FXML
    public void saveAdjustedAppointmentDateTime() throws SQLException {

        // retrieve time text from form
        String dateStart = dateStartInputTextField.getText();
        String timeStart = timeStartInputTextField.getText();
        String dateEnd = dateEndInputTextField.getText();
        String timeEnd = timeEndInputTextField.getText();

        // convert locally formatted times to UTC timestamps.
        Timestamp timestampStart = TimeUtil.formValueToUTCTimestamp(dateStart, timeStart);
        Timestamp timestampEnd = TimeUtil.formValueToUTCTimestamp(dateEnd, timeEnd);

        // convert UTC timestamps to EST formatted times for checking against business hours
        Timestamp startEST = TimeUtil.formValueToESTTimestamp(dateStart, timeStart);
        Timestamp endEST = TimeUtil.formValueToESTTimestamp(dateEnd, timeEnd);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String startTimeEST = timeFormat.format(startEST.getTime());
        String endTimeEST = timeFormat.format(endEST.getTime());

        // use formatted EST times to check if valid times and within business hours. Returns from function if invalid
        if (!TimeUtil.areDatesBetween(Time.valueOf(startTimeEST),Time.valueOf(endTimeEST), Constants.BUSINESS_HOURS_START_EST, Constants.BUSINESS_HOURS_END_EST)) {
            FxUtil.displayInfoDisplayText("One or more proposed appointment times are outside of business hours (8am-10pm EST).", true, infoDisplayText);
            return;
        }

        // select currently selected appointment from TableView
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            if (appointmentDAO.hasOverlappingAppointments(timestampStart, timestampEnd)) {
                FxUtil.displayInfoDisplayText("The proposed appointment time conflicts with other appointments.", true, infoDisplayText);
                return;
            }
        } else {
            if (appointmentDAO.hasOverlappingAppointments(timestampStart, timestampEnd, appointment)) {
                FxUtil.displayInfoDisplayText("The proposed appointment time conflicts with other appointments.", true, infoDisplayText);
                return;
            }
        }

        // create new appointment object
        assert appointment != null;
        Appointment updatedAppointment = new Appointment(appointment.getId(), appointment.getTitle(), appointment.getDescription(), appointment.getLocation(), appointment.getType(),
                timestampStart, timestampEnd, appointment.getCreateDate(), appointment.getCreatedBy(), appointment.getLastUpdate(), appointment.getLastUpdatedBy(), appointment.getCustomerId(),
                appointment.getUserId(), appointment.getContactId());

        // send updated appointment
        appointmentDAO.updateAppointment(updatedAppointment);

        // fill in TableView with appointments
        if (byMonthRadioButton.isSelected()) {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } else {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
        }

        // disable buttons
        adjustAppointmentSaveButton.setDisable(true);
        cancelButton.setDisable(true);
    }

    /**
     * display report information for currently selected Appointment Type
     * @throws SQLException thrown if invalid type is attempted to retrieve appintments for is called
     */
    @FXML
    public void displayNumberOfAppointmentsByType() throws SQLException {
        String type = typeReportComboBox.getSelectionModel().getSelectedItem();
        typeReportText.setText(Integer.toString(appointmentDAO.getAppointmentsByType(type).size()));
    }

    /**
     * display report information for currently selected month/year (default year is 2023)
     * @throws SQLException thrown if invalid month/year is used
     */
    @FXML
    public void displayNumberOfAppointmentsByMonthYear() throws SQLException {
        String selectedMonth = byMonthReportMonthComboBox.getSelectionModel().getSelectedItem();
        int month = TimeUtil.monthStringToInt(selectedMonth);
        int year = byMonthReportYearSpinner.getValue();

        byMonthReportText.setText(Integer.toString(appointmentDAO.getAppointmentsByMonthYear(month, year).size()));
    }

    /**
     * generates report information for currently selected country from Country combo box
     * @throws SQLException thrown if invlaid country is entered into CountryDAO::getCountryAppointmentCount(country)
     */
    @FXML
    public void displayNumberOfAppointmentsByCountry() throws SQLException {
        String country = byCountryReportComboBox.getSelectionModel().getSelectedItem();
        byCountryReportText.setText(Integer.toString(countryDAO.getCountryAppointmentCount(country)));
    }

    /**
     * navigates to ContactSchedule.fxml and sets up form to reload if returning to it from ContactSchedule
     * @param actionEvent event propagated after clicking on Contacts button
     * @throws IOException thrown if ContactSchedul.fxml is not found within Resources folder
     */
    @FXML
    public void navigateToContactSchedule(ActionEvent actionEvent) throws IOException {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateTo("ContactSchedule.fxml", stage, this::resetAppointmentScheduleByMonth);
    }
}

