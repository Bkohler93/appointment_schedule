package com.example.appointment_schedule.controller;


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
    private final UserDAO userDao = new UserDAOImpl();
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Text errorTextField;
    @FXML
    private Text titleText;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label locationLabel;
    @FXML
    private Label zoneIdLabel;

    /** LAMBDA EXPRESSIONS HERE!
     * sets form fields and adds listeners on Login.fxml. Uses lambda expressions to clear any errors when
     * modifying a text field.
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
        passwordTextField.textProperty().addListener((observableValue, oldValue, newValue) ->
            errorTextField.setText(""));
    }

    /**
     * uses currently entered username/password to retrieve a user with matching credentials. If no user is found
     * an error is displayed and the program does not navigate to the next form.
     * @param actionEvent event propagated after clicking on `Login` button
     */
    @FXML
    public void login(ActionEvent actionEvent) {
        //retrieve form fields
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // attempt to retrieve user from DB. Display error if no
        try {
            User user = userDao.getUserByUsernamePw(username, password);
            if (user == null) {
                errorTextField.setText(Localization.text("Check your username and password"));
                Logger.login(username, password, false);
            } else {
                Auth.login(user);
                Logger.login(username, password, true);
                System.out.println("logged in as " + user.getUserName());

                // navigate to Appointment View
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                FxUtil.navigateTo("AppointmentSchedule.fxml", stage, null);
            }
        } catch (SQLException | IOException e) {
            errorTextField.setText(Localization.text("Check your username and password"));
        }
    }

    /**
     * Attempts to add a new user to the database using username and password fields.
     */
    @FXML
    public void register() {
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
}
