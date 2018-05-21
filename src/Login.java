package reservations;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;

public class Login extends Scene {

    public Login(GridPane grid, Stage stage, Database database) {
        super(grid, 350, 200);
        FXHelper.prepStage(stage, 400, 500, 225, 250);

        LOGGER.addHandler(Log.fileHandler);
        LOGGER.setLevel(Level.ALL);

        this.getStylesheets().add("File:../ressources/stylesheet.css");
        this.grid = grid;
        this.database = database;

        grid.setPadding(new Insets(10, 10, 20, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        final Text scenetitle = new Text("Ticket Reservations");
        // scenetitle.setFont(Font.font("Coiny", FontWeight.NORMAL, 20));
        scenetitle.setId("headingText");
        grid.add(scenetitle, 0, 0, 2, 1);
        grid.setHalignment(scenetitle, HPos.CENTER);

        invalidLogin = new Label("Invalid username and/or password!");
        invalidLogin.setId("invalidLogin");
        invalidLogin.setVisible(false);
        grid.setHalignment(invalidLogin, HPos.CENTER);
        grid.add(invalidLogin, 0, 1, 3, 1);

        final Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 2);
        grid.setHalignment(userLabel, HPos.CENTER);

        // Name Input
        userInput = new TextField("max.muster@gmail.com");
        userInput.setPromptText("Your username");
        grid.add(userInput, 1, 2);

        // Password Label
        final Label passLabel = new Label("Password:");
        grid.add(passLabel, 0, 3);
        grid.setHalignment(passLabel, HPos.CENTER);

        // Password Input
        passInput = new PasswordField();
        passInput.setPromptText("Your password");
        grid.add(passInput, 1, 3);

        HBox loginExit = new HBox();

        // Login
        loginButton = new Button("Log In");
        // grid.add(loginButton, 1, 3);

        // Exit
        exitButton = new Button("Exit");
        // grid.add(exitButton, 2, 3);
        loginExit.setSpacing(10);
        loginExit.getChildren().addAll(loginButton, exitButton);
        grid.add(loginExit, 1, 4);

        info = new Button();
        ImageView infoView = new ImageView(new Image("File:../ressources/fonts-images/info.png"));
        infoView.setFitHeight(25);
        infoView.setFitWidth(25);
        info.setGraphic(infoView);
        info.setId("transButton");
        grid.add(info, 0, 4);

        for (int i = 0; i < 4; i++) {

            RowConstraints rowCons = new RowConstraints();
            rowCons.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rowCons);
        }

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(30);
        col.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().add(col);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(70);
        grid.getColumnConstraints().add(col1);

        final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (event.getSource() != info) {
                    callMenu(stage, event.getSource());
                } else {
                    new InfoTab(stage);
                }

            }
        };

        info.setOnAction(eventHandler);
        exitButton.setOnAction(eventHandler);
        loginButton.setOnAction(eventHandler);
        pressKey(loginButton, stage);
        pressKey(exitButton, stage);

        
         this.setOnKeyPressed(new EventHandler<KeyEvent>(){
         
         @Override public void handle(KeyEvent key){
         if(key.getCode().equals(KeyCode.ENTER)){ callMenu(stage, loginButton); }
         
         } });
         

        /*this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (FXHelper.infoComb.match(key)) {
                    new InfoTab(stage);
                }
                if (FXHelper.exitComb.match(key)) {
                    System.exit(0);
                }
            }
        });*/

    }

    // login/exit with enter
    private void pressKey(Button button, Stage stage) {
        button.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    callMenu(stage, button);
                }
            }

        });
    }

    // to handle buttons with keys and mouse
    private void callMenu(Stage stage, Object button) {
        if (button == loginButton && isAdmin(userInput.getText()) == false) {
            if (loginChecker(userInput.getText(), passInput.getText()) == true) {
                LOGGER.log(Level.INFO, "logged in as user");
                BorderPane menuBorder = new BorderPane();
                // goto menu
                new Menu(menuBorder, database);
                stage.close();

            } else {
                loginFalse();
            }
        } else if (button == loginButton && isAdmin(userInput.getText()) == true) {

            if (loginChecker(userInput.getText(), passInput.getText()) == true) {
                LOGGER.log(Level.INFO, "logged in as Admin");
                // goto menu
                new AdminControls(new HBox(), database);
                stage.close();
            } else {
                loginFalse();
            }
        }

        if (button == exitButton) {
            stage.close();
        }
    }

    private boolean loginChecker(String user, String password) {
        if (database.loginQuery(user).equals(password)) {
            loggedInUser = user;
            return true;
        } else {
            return false;
        }
    }

    private boolean isAdmin(String user) {
        if (user.equals("admin")) {
            return true;
        } else {
            return false;
        }
    }

    private void loginFalse() {
        invalidLogin.setVisible(true);
        passInput.clear();
        userInput.clear();
        userInput.setPromptText("Try again");
        passInput.setPromptText("Try again");
        userInput.requestFocus();

    }

    private GridPane grid = new GridPane();
    private Database database;
    private PasswordField passInput;
    private TextField userInput;
    private Label invalidLogin;
    private Button loginButton;
    private Button exitButton;
    // for licens and key information
    private Button info;
    // for menu
    public static String loggedInUser;

    public final static Logger LOGGER = Logger.getLogger(Login.class.getName());

}
