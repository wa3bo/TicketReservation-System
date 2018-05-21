package reservations;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import reservations.FXHelper;
import reservations.HandleCSV;
import reservations.Information;
import reservations.Login;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ChoiceDialog;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;


public class Summary extends Stage {

    public Summary(VBox complete, Information summary, Database database) {
        LOGGER.addHandler(Log.fileHandler);
		LOGGER.setLevel(Level.ALL);
        Scene scene = new Scene(complete, 500, 500);
        scene.getStylesheets().add("File:../ressources/stylesheet.css");
        final Label sceneTitle = new Label("Thanks for Booking :)");
        sceneTitle.setAlignment(Pos.TOP_CENTER);
        sceneTitle.setId("headingText");
        this.database = database;

        HBox vbox = new HBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
       
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        HBox userBox = new HBox();
        final Label user = new Label("User: ");
        grid.add(user, 0, 0);
        final Label getUser = new Label(summary.getUser());
        grid.add(getUser, 1, 0);
       
        HBox dateBox = new HBox();
        final Label date = new Label("Date: ");
        grid.add(date, 0, 1);
        final Label getDate = new Label(summary.getDate());
        grid.add(getDate, 1, 1);
        
        HBox timeBox = new HBox();
        final Label time = new Label("Time: ");
        grid.add(time, 0, 2);
        final Label getTime = new Label(summary.getTime());
        grid.add(getTime, 1, 2);
      
        HBox movieBox = new HBox();
        final Label movie = new Label("Movie: ");
        grid.add(movie, 0, 3);
        final Label getMovie = new Label(summary.getMoviename());
        grid.add(getMovie, 1, 3);
       
        HBox seatsBox = new HBox();
        final Label seats = new Label("Seats: ");
        grid.add(seats, 0, 4);
        final Label getSeats = new Label(summary.getSeats());
        grid.add(getSeats, 1, 4);
       
        Button feedbackButton = new Button("Feedback");
        feedbackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showChoiceDialog();
            }
        });

        HBox box = FXHelper.createHboxSummary(continueBook, feedbackButton);
        HBox box1 = FXHelper.createHboxSummary(logout, exitButton);

        complete.setAlignment(Pos.CENTER);
        complete.getChildren().addAll(sceneTitle, grid, box, box1);

        final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {

                // go back to login
                if (e.getSource() == logout) {
                    logout();
                }
                if (e.getSource() == exitButton) {
                    LOGGER.log(Level.INFO, "Exit Application");	
                    Summary.this.close();
                }

                if (e.getSource() == continueBook) {
                    LOGGER.log(Level.INFO, "continue booking");	
                    new Menu(new BorderPane(), database);
                    Summary.this.close();
                }

            }
        };

        exitButton.setOnAction(eventHandler);
        logout.setOnAction(eventHandler);
        continueBook.setOnAction(eventHandler);

        this.setTitle("Summary");
        this.setScene(scene);
        FXHelper.prepStage(this, 400, 600, 400, 600);
        this.show();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ESCAPE)) {
                    logout();
                }
                if (FXHelper.exitComb.match(key)) {
                    System.exit(0);
                }
            }
        });

    }

    private void logout() {
        LOGGER.log(Level.INFO, "Logout");		
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Cinema Reservations");
        GridPane gridPane = new GridPane();
        Login login = new Login(gridPane, primaryStage, database);
        primaryStage.setScene(login);
        primaryStage.show();
        Summary.this.close();
    }

    private void showChoiceDialog() {

        ChoiceDialog<String> dialog = new ChoiceDialog<String>("Great :)", "Okay :/", "Bad :(");
        LOGGER.log(Level.INFO, "showing feedback dialog");	
        dialog.setTitle("Feedback");
        dialog.setHeaderText(null);
        dialog.setContentText("Rate this application:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            HandleCSV.writeCSV(result.get(), Login.loggedInUser, "../ressources/csv/feedback.csv");
        }
    }

    private final Button exitButton = new Button("exit");
    private final Button continueBook = new Button("continue booking");
    private final Button logout = new Button("logout");
    private Database database;
    public final static Logger LOGGER = Logger.getLogger(Summary.class.getName());
}