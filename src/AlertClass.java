package reservations;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertClass {
    public static void cantInsertAlert(String info) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Datarow already exists!");
        alert.setContentText("Can't insert your datarow with data on index 0: " + info);
        alert.showAndWait();
    }

    public static void formatAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Wrong format");
        alert.setContentText("nothing has to be emtpy, \ntime format to insert: hh:mm");
        alert.showAndWait();
    }

    public static void adminPassAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Password is invalid or null");
        alert.setContentText("insert your strong password!");
        alert.showAndWait();
    }

    public static void successInfoAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success Dialog");
        alert.setHeaderText("Data added to Database");
        alert.setContentText("Continue your activities!");
        alert.showAndWait();
    }

    public static void deleteAdmin() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("HEY! Seriously?");
        alert.setContentText("you can't delete the admin");
        alert.showAndWait();
    }

    public static void seatAlert() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("You can't book if no seat is selected");
        alert.setContentText("Please select a seat!");

        alert.showAndWait();
    }
}