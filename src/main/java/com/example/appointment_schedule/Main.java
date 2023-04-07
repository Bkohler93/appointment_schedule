package com.example.appointment_schedule;

import com.example.appointment_schedule.dao.DBConnection;
import com.example.appointment_schedule.localization.Localization;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("Login.fxml")));
        primaryStage.setTitle(Localization.text("Appointment Scheduler"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Localization.initialize();

        DBConnection.openConnection();
        launch(args);
    }
}
