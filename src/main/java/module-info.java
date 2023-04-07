module com.example.appointment_schedule {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.appointment_schedule to javafx.fxml;
    opens com.example.appointment_schedule.controller to javafx.fxml;
    opens com.example.appointment_schedule.model to javafx.base;
    exports com.example.appointment_schedule;
}