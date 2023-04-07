package com.example.appointment_schedule.util;


import com.example.appointment_schedule.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class FxUtil {

    /**
     * navigates to a form and sets up redirect on close request back to main form
     * @param resourceId the name of the fxml file to load
     * @param stage the stage the main form is the scene for
     * @throws IOException thrown when resource with resourceId doesn't exist
     */
    public static void navigateTo(String resourceId, Stage stage, Callback callback) throws IOException {
        Parent scene = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(resourceId)));
        stage.hide();
        Stage newStage = new Stage();
        newStage.setScene(new Scene(scene));
        stage.setOnShowing(e -> {
            if (callback != null) {
                callback.call();
            }
        });
        newStage.setOnCloseRequest(windowEvent -> stage.show());
        newStage.show();
    }

    /**
     * open a new form in a new window so that when the new form's window is exited, the user is returned
     * to the main form.
     * @param stage TODO
     * @param loader the FXMLLoader with loaded resource
     */
    public static void navigateToWithData(Stage stage, FXMLLoader loader, Callback callback) {
        Parent scene = loader.getRoot();
        stage.hide();
        Stage newStage = new Stage();

        newStage.setScene(new Scene(scene));

        // form being navigated to can set a method to execute
        // when page is being shown again after navigating to any future forms.
        // Ex. User is on the form with full list of customers and adds another customer, once
        //      the new customer is added the application leaves the Add Customer form and returns
        //      to the list of customers, triggering this `setOnShowing` to refetch
        //      the updated customer list.
        stage.setOnShowing(e -> {
            if (callback != null) {
                callback.call();
            }
        });

        // works in conjunction with `setOnShowing` after escaping from a form.
        newStage.setOnCloseRequest(windowEvent -> stage.show());

        newStage.show();
    }

    /**
     * used to call a method when a window reappears after closing the current window
     */
    @FunctionalInterface
    public interface Callback {
        void call();
    }

    /**
     * Opens an alert dialog and waits for user to confirm or cancel the pending action
     * @param action describes what action is about to take place
     * @param msg the question to ask the user
     * @return true if user confirmed action or false if user cancelled
     */
    public static boolean confirmAction(String action, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(action);
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void displayDeleteErrorAlert(String itemType, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Delete " + itemType + " Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void displayInfoDisplayText(String info, boolean isError, Text infoDisplayText) {
        if (isError) {
            infoDisplayText.setFill(Color.RED);
        } else {
            infoDisplayText.setFill(Color.GREEN);
        }
        infoDisplayText.setText(info);
    }

    public static void clearInfoDisplayText(Text infoDisplayText) {
        infoDisplayText.setText("");
    }
}
