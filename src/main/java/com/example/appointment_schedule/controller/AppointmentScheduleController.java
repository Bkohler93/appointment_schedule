package com.example.appointment_schedule.controller;

import com.example.appointment_schedule.Constants;
import com.example.appointment_schedule.Main;
import com.example.appointment_schedule.dao.appointment.AppointmentDAO;
import com.example.appointment_schedule.dao.appointment.AppointmentDAOImpl;
import com.example.appointment_schedule.dao.contact.ContactDAO;
import com.example.appointment_schedule.dao.contact.ContactDAOImpl;
import com.example.appointment_schedule.model.Appointment;
import com.example.appointment_schedule.model.User;
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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

import static com.example.appointment_schedule.util.FxUtil.clearInfoDisplayText;
import static com.example.appointment_schedule.util.FxUtil.displayInfoDisplayText;

public class AppointmentScheduleController implements Initializable {
    private final int dateTextFieldLength = 10;
    private final int timeTextFieldLength = 5;
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final ContactDAO contactDAO = new ContactDAOImpl();
    public Button adjustAppointmentSaveButton;
    public AnchorPane anchorPane;
    public Text upcomingAptNotificationText;
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
    private int selectedMonth;
    private int selectedYear;
    private String selectedWeekStartDate;
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
    private User user;
    public void sendUser(User user) {
        this.user = user;
    }

    public void onActionCustomersButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("CustomerList.fxml"));
        loader.load();

        CustomerListController customerListController = loader.getController();

        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

        FxUtil.navigateToWithData(stage, loader, customerListController::loadCustomers);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        byMonthRadioButton.setSelected(true);
        cancelButton.setDisable(true);
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

        LocalDate now = LocalDate.now();
        selectedMonth = now.getMonth().getValue();
        String selectedMonthStr = Month.of(selectedMonth).name();
        selectedYear = now.getYear();

        selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

        try {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Appointment upcomingAppointment = null;
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

        appointmentTableView.setOnMouseClicked(event -> {
            adjustAppointmentSaveButton.setDisable(true);
            Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
            cancelButton.setDisable(false);
            clearInfoDisplayText(infoDisplayText);
            if (appointment != null) {
                Timestamp timestampStart = appointment.getStart();
                Timestamp timestampEnd = appointment.getEnd();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String dateStart = dateFormat.format(timestampStart);
                String dateEnd = dateFormat.format(timestampEnd);
                String timeStart = timeFormat.format(timestampStart);
                String timeEnd = timeFormat.format(timestampEnd);

                dateStartInputTextField.setText(dateStart);
                timeStartInputTextField.setText(timeStart);
                dateEndInputTextField.setText(dateEnd);
                timeEndInputTextField.setText(timeEnd);
            }
        });

        appointmentTableView.setOnKeyPressed(event -> {
            adjustAppointmentSaveButton.setDisable(true);
            clearInfoDisplayText(infoDisplayText);
            Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
            if (appointment != null) {
                Timestamp timestampStart = appointment.getStart();
                Timestamp timestampEnd = appointment.getEnd();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String dateStart = dateFormat.format(timestampStart);
                String dateEnd = dateFormat.format(timestampEnd);
                String timeStart = timeFormat.format(timestampStart);
                String timeEnd = timeFormat.format(timestampEnd);

                dateStartInputTextField.setText(dateStart);
                timeStartInputTextField.setText(timeStart);
                dateEndInputTextField.setText(dateEnd);
                timeEndInputTextField.setText(timeEnd);
            }
        });


        timeStartInputTextField.setOnKeyTyped(keyEvent -> { onKeyTypedDateTimeTextField(); });
        timeEndInputTextField.setOnKeyTyped(keyEvent -> { onKeyTypedDateTimeTextField(); });
        dateStartInputTextField.setOnKeyTyped(keyEvent -> { onKeyTypedDateTimeTextField(); });
        dateEndInputTextField.setOnKeyTyped(keyEvent -> { onKeyTypedDateTimeTextField(); });

    }

    private void onKeyTypedDateTimeTextField() {
        clearInfoDisplayText(infoDisplayText);
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        if (appointment == null) { return; }

        String dateStart = dateStartInputTextField.getText();
        String timeStart = timeStartInputTextField.getText();
        String dateEnd = dateEndInputTextField.getText();
        String timeEnd = timeEndInputTextField.getText();

        if (!(TimeUtil.isValidDate(dateStart) && TimeUtil.isValidTime(timeStart) && TimeUtil.isValidDate(dateEnd) && TimeUtil.isValidTime(timeEnd))) {
            adjustAppointmentSaveButton.setDisable(true);
            return;
        }

        Timestamp timestampStart = TimeUtil.formValueToTimestamp(dateStart, timeStart);
        Timestamp timestampEnd = TimeUtil.formValueToTimestamp(dateEnd, timeEnd);

        // disable button if all adjust date/times are same as currently selected appointment.
        adjustAppointmentSaveButton.setDisable(timestampStart.equals(appointment.getStart()) && timestampEnd.equals(appointment.getEnd()));
    }

    public void onActionPreviousViewButton(ActionEvent actionEvent) {
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);
        if (byMonthRadioButton.isSelected()) {
            selectedYear = selectedMonth == 1 ? selectedYear - 1 : selectedYear;
            selectedMonth = selectedMonth == 1 ? 12 : selectedMonth - 1;
            String selectedMonthStr = Month.of(selectedMonth).name();

            selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(selectedWeekStartDate, formatter);
            LocalDate nextWeek = date.plusWeeks(-1);
            selectedWeekStartDate = nextWeek.format(formatter);
            selectedTimeLabel.setText("Week of " + selectedWeekStartDate);

            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void onActionNextViewButton(ActionEvent actionEvent) {
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);
        if (byMonthRadioButton.isSelected()) {
            selectedYear = selectedMonth == 12 ? selectedYear + 1 : selectedYear;
            selectedMonth = selectedMonth == 12 ? 1 : selectedMonth + 1;
            String selectedMonthStr = Month.of(selectedMonth).name();

            selectedTimeLabel.setText(selectedMonthStr.substring(0,1) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(selectedWeekStartDate, formatter);
            LocalDate nextWeek = date.plusWeeks(1);
            selectedWeekStartDate = nextWeek.format(formatter);
            selectedTimeLabel.setText("Week of " + selectedWeekStartDate);

            try {
                appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void onActionByMonthRadioButton(ActionEvent actionEvent) {
        clearDateTimeInputTextFields();
        clearInfoDisplayText(infoDisplayText);
        cancelButton.setDisable(true);
        byWeekRadioButton.setSelected(false);
        byMonthRadioButton.setSelected(true);

        String selectedMonthStr = Month.of(selectedMonth).name();
        selectedTimeLabel.setText(selectedMonthStr.charAt(0) + selectedMonthStr.substring(1).toLowerCase() + " " + selectedYear);

        try {
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onActionByWeekRadioButton(ActionEvent actionEvent) throws SQLException {
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
        appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
    }

    private void clearDateTimeInputTextFields() {
        timeStartInputTextField.setText("");
        timeEndInputTextField.setText("");
        dateStartInputTextField.setText("");
        dateEndInputTextField.setText("");
    }

    public void onActionModifyButton(ActionEvent actionEvent) throws IOException, SQLException {
        Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        clearInfoDisplayText(infoDisplayText);
        if (appointment != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("Appointment.fxml"));
            loader.load();

            AppointmentController appointmentController = loader.getController();
            appointmentController.sendAppointment(appointment);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            FxUtil.navigateToWithData(stage, loader, this::resetAppointmentScheduleByMonth);
        } else {
//            displayInfoDisplayText("Select an appointment to modify.", true, infoDisplayText);
        }
    }

    public void resetAppointmentScheduleByMonth() {
        try {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onActionAddButton(ActionEvent actionEvent) throws IOException {
        clearInfoDisplayText(infoDisplayText);
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        FxUtil.navigateTo("Appointment.fxml", stage, this::resetAppointmentScheduleByMonth);
    }

    public void onActionCancelButton(ActionEvent actionEvent) throws SQLException {
        Appointment appointmentToCancel = appointmentTableView.getSelectionModel().getSelectedItem();
        appointmentDAO.deleteAppointment(appointmentToCancel);

        displayInfoDisplayText("Appointment ID[" + appointmentToCancel.getId() + "]: " + appointmentToCancel.getType() + " cancelled", false, infoDisplayText);
    }

    public void onActionAdjustAppointmentSaveButton(ActionEvent actionEvent) throws SQLException {
        String dateStart = dateStartInputTextField.getText();
        String timeStart = timeStartInputTextField.getText();
        String dateEnd = dateEndInputTextField.getText();
        String timeEnd = timeEndInputTextField.getText();

        Timestamp timestampStart = TimeUtil.formValueToUTCTimestamp(dateStart, timeStart);
        Timestamp timestampEnd = TimeUtil.formValueToUTCTimestamp(dateEnd, timeEnd);

        Timestamp startEST = TimeUtil.formValueToESTTimestamp(dateStart, timeStart);
        Timestamp endEST = TimeUtil.formValueToESTTimestamp(dateEnd, timeEnd);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String startTimeEST = timeFormat.format(startEST.getTime());
        String endTimeEST = timeFormat.format(endEST.getTime());
        if (!TimeUtil.areDatesBetween(Time.valueOf(startTimeEST),Time.valueOf(endTimeEST), Constants.BUSINESS_HOURS_START_EST, Constants.BUSINESS_HOURS_END_EST)) {
            FxUtil.displayInfoDisplayText("One or more proposed appointment times are outside of business hours (8am-10pm EST).", true, infoDisplayText);
            return;
        }


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

        Appointment updatedAppointment = new Appointment(appointment.getId(), appointment.getTitle(), appointment.getDescription(), appointment.getLocation(), appointment.getType(),
                timestampStart, timestampEnd, appointment.getCreateDate(), appointment.getCreatedBy(), appointment.getLastUpdate(), appointment.getLastUpdatedBy(), appointment.getCustomerId(),
                appointment.getUserId(), appointment.getContactId());

        appointmentDAO.updateAppointment(updatedAppointment);

        if (byMonthRadioButton.isSelected()) {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByMonthYear(selectedMonth, selectedYear));
        } else {
            appointmentDAO.getAllAppointments();
            appointmentTableView.setItems(appointmentDAO.getAppointmentsByWeek(selectedWeekStartDate));
        }
        adjustAppointmentSaveButton.setDisable(true);
    }
}

