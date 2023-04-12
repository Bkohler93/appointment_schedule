package com.example.appointment_schedule;

import com.example.appointment_schedule.dao.DBConnection;
import com.example.appointment_schedule.localization.Localization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * starts the program by displaying Login.fxml, opens the DB connection and initializes Localization
 */
public class Main extends Application {

    /**
     * Loads the first page onto a stage in order to display it.
     * @param primaryStage stage to load scene onto.
     * @throws IOException thrown if Login.fxml is not found in `resources` directory
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Login.fxml")));
        primaryStage.setTitle(Localization.text("Appointment Scheduler"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(windowEvent -> DBConnection.closeConnection());
    }

    /**
     * initializes localization, opens DB connection and launches application
     * @param args arguments to launch with
     */
    public static void main(String[] args) {
        Localization.initialize();

        DBConnection.openConnection();
        launch(args);
    }
}
