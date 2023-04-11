package com.example.appointment_schedule.controller;


import com.example.appointment_schedule.Main;
import com.example.appointment_schedule.auth.Auth;
import com.example.appointment_schedule.dao.user.UserDAO;
import com.example.appointment_schedule.dao.user.UserDAOImpl;
import com.example.appointment_schedule.localization.Localization;
import com.example.appointment_schedule.model.User;
import com.example.appointment_schedule.util.FxUtil;
import com.example.appointment_schedule.util.Logger;
import com.example.appointment_schedule.util.TimeUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;

public class LoginController {
    @FXML
    public TextField usernameTextField;
    @FXML
    public TextField passwordTextField;
    @FXML
    public Text errorTextField;
    private final UserDAO userDao = new UserDAOImpl();
    @FXML
    public Text titleText;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public Button loginButton;
    @FXML
    public Button registerButton;
    @FXML
    public Label locationLabel;
    @FXML
    public Label zoneIdLabel;

    /**
     * sets form fields and adds listeners on Login.fxml
     */
    @FXML
    public void initialize() {
        titleText.setText(Localization.text("Appointment Scheduling App"));
        usernameLabel.setText(Localization.text("Username"));
        passwordLabel.setText(Localization.text("Password"));
        loginButton.setText(Localization.text("Login"));
        registerButton.setText(Localization.text("Register"));
        locationLabel.setText(Localization.text("Location") + ": ");
        zoneIdLabel.setText(ZoneId.systemDefault().toString());

        usernameTextField.textProperty().addListener((observableValue, oldValue, newValue) ->
                errorTextField.setText(""));

        // hides password by turning all characters into "*" in text field, but saving password in `password` variable
        passwordTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            errorTextField.setText("");
        });
    }


    public void onActionLoginButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        try {
            User user = userDao.getUserByUsernamePw(username, password);
            if (user == null) {
                errorTextField.setText(Localization.text("Check your username and password"));
                Logger.login(username, password, false);
            } else {
                Auth.login(user);
                Logger.login(username, password, true);
                System.out.println("logged in as " + user.getUserName());
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(Main.class.getResource("AppointmentSchedule.fxml"));
                loader.load();

                AppointmentScheduleController appointmentScheduleController = loader.getController();

                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                FxUtil.navigateToWithData(stage, loader, null);
                // send to AppointmentSchedule with user data
            }
        } catch (SQLException | IOException e) {
            errorTextField.setText(Localization.text("Check your username and password"));
        }
    }

    /**
     * Attempts to add a new user to the database using username and password fields.
     * @param actionEvent event propagated from button press
     */
    @FXML
    public void onActionRegisterButton(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        Timestamp utcTimestamp = TimeUtil.UTCTimestampNow();

        User newUser = new User(-1, username, password, utcTimestamp, username, utcTimestamp, username);

        try {
            userDao.addUser(newUser);
        } catch (SQLException e) {
            errorTextField.setText(Localization.text("A user already exists with that username") + ".");
        }
    }

    @FXML
    public void onActionUsernameTextField(ActionEvent actionEvent) {
        errorTextField.setText("");
    }
}
