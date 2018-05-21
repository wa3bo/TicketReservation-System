package reservations;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx.scene.layout.GridPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import java.util.Optional;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;


import java.io.*;

public class Gui extends Application {

    public void start(Stage primaryStage) {
        
        LOGGER.addHandler(Log.fileHandler);
        LOGGER.setLevel(Level.ALL);
        
        stage = primaryStage;
        String admin = checkPasswordSet();
        String tmpAdmin = "";

        // can't continue without a admin password
        while (admin == null) {
            AlertClass.adminPassAlert();
            tmpAdmin = checkPasswordSet();
            admin = tmpAdmin;
        }

        database = new Database();

        if (!admin.equals("")) {
            LOGGER.log(Level.INFO, "Insert admin in database");
            database.insertSimplyTable("User", "admin", admin);
        }

        GridPane grid = new GridPane();
        Login login = new Login(grid, stage, database);
        stage.setTitle("Login");
        stage.setScene(login); // database übergeben
        // stage.setResizable(false); //nicht anpassbar

        stage.show();

    }

    // set adminPassword (if the database file doesn't exists)
    private String checkPasswordSet() {

        File file = new File("../data.db");

        if (file.exists()) {
            LOGGER.log(Level.INFO, "Database already exists");
            return "";
        } else {

            Dialog dialog = new Dialog<>();
            DialogPane pane = new DialogPane();

            dialog.setTitle("Set AdminPassword");
            dialog.setResizable(false);

            ButtonType buttonTypeOk = new ButtonType("Okay");

            pane.getButtonTypes().setAll(buttonTypeOk);
            Button buttonOk = (Button) pane.lookupButton(buttonTypeOk);

            Label label = new Label("Enter your\nstrong password: ");
            Label labelRep = new Label("Repeat your password: ");

            PasswordField password = new PasswordField();
            password.setPromptText("Your password");
            password.requestFocus();

            PasswordField passwordRepeat = new PasswordField();
            passwordRepeat.setPromptText("repeat your password");

            GridPane grid = new GridPane();
            grid.add(label, 1, 1);
            grid.add(password, 2, 1);
            grid.add(labelRep, 1, 2);
            grid.add(passwordRepeat, 2, 2);

            pane.setContent(grid);
            pane.setHeaderText("Set your Password. Remeber it.\nLater on you can't change it.");
            // pane.getButtonTypes().setAll(buttonTypeOk);
            dialog.setDialogPane(pane);

            // dialog.showAndWait();

            dialog.showAndWait();
            String result = password.getText();
            String checkPassword = passwordRepeat.getText();

            if (checkResult(result, checkPassword) == true) {
                return result;
            } else {
                return null;
            }

            /*
             * buttonOk.setOnKeyPressed(new EventHandler<KeyEvent>() {
             * 
             * @Override public void handle(KeyEvent key){
             * if(key.getCode().equals(KeyCode.ENTER)){ System.out.println("pass: " +
             * result); System.out.println("pass rep: " + checkPassword);
             * if(checkResult(result, checkPassword) == true){ System.out.println("haha");
             * dialog.setResult(result); status = true; System.out.println("status innen: "
             * + status); } else { System.out.println(""); dialog.setResult("false");
             * status = false; //System.out.println(dialog.getResult().toString()); } } }
             * });
             * 
             * Optional<ButtonType> res = dialog.showAndWait();
             * 
             * System.out.println("status: " + status); if(status != false){
             * System.out.println(""); return res.toString(); } else {
             * System.out.println(""); return null; }
             */

            // System.out.println("haha");

        }

    }

    private boolean checkResult(String res, String checkPass) {
        if (res.equals(checkPass) && !res.equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String args[]) {
        launch(args);
    }

    private Stage stage;
    public Database database;
    // private boolean status;
    public final static Logger LOGGER = Logger.getLogger(Gui.class.getName());

}
