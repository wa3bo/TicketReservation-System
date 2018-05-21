package reservations;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import reservations.AlertClass;
import reservations.Database;
import reservations.FXHelper;
import reservations.HandleCSV;
import reservations.HandleTableView;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class AdminControls extends Stage {

    public AdminControls(HBox hBox, Database database) {

        LOGGER.addHandler(Log.fileHandler);
        LOGGER.setLevel(Level.ALL);
        this.database = database;
        stage = this;

        Scene scene = new Scene(hBox, 800, 400);
        FXHelper.prepStage(stage, 840, 840, 400, 400);
        scene.getStylesheets().add("File:../ressources/stylesheet.css");

        hBox.getChildren().addAll(createVBox("User"), createVBox("Movies"), vBoxOverview());
        hBox.setPadding(new Insets(10, 20, 10, 20));

        final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e) {

                // go back to login
                if (e.getSource() == logout) {
                    logout();
                }
                if (e.getSource() == exitButton) {
                    LOGGER.log(Level.INFO, "Exit Application");
                    AdminControls.this.close();
                }

            }
        };

        exitButton.setOnAction(eventHandler);
        logout.setOnAction(eventHandler);

        this.setTitle("Admin Controls");
        this.setScene(scene);
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
        AdminControls.this.close();
    }

    // create headings for Users and Movies
    private Label createTitle(String handler) {
        final Label sideTitle = new Label(String.format("Control %s", handler));
        sideTitle.setAlignment(Pos.TOP_CENTER);
        sideTitle.setPadding(new Insets(20, 0, 20, 0));
        sideTitle.setId("headingAdmin");

        return sideTitle;

    }

    // to create all buttons for Users and Movies
    private VBox createVBox(String handler) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 20, 20, 0));
        Button handle = new Button(String.format("Handle %s", handler));
        handle.setId("handle");
        Button addCSV = new Button(String.format("Add %s from \na CSV file", handler));
        addCSV.setId("addCSV");

        vBox.getChildren().addAll(createTitle(handler), handle, addCSV);
        vBox.setMinHeight(400);
        HBox box = new HBox();
        box.setPadding(new Insets(103, 0, 0, 0));

        if (handler.equals("User")) {
            box.getChildren().addAll(logout);
            vBox.getChildren().add(box);
        } else {
            box.getChildren().addAll(exitButton);
            vBox.getChildren().add(box);
        }
        vBox.setSpacing(20);
        handle.setOnAction(handleButtons(handle, handler));

        addCSV.setOnAction(handleButtons(addCSV, handler));

        return vBox;
    }

    private VBox vBoxOverview() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 0, 20, 0));
        vBox.getChildren().addAll(createTitle("Watch Overview"), createOverview());
        return vBox;
    }

    private EventHandler<ActionEvent> handleButtons(Button btn, String handler) {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                // to different movies and user
                status = handler;
                if (btn.getId().equals("handle")) {
                    HandleTableView tableView = new HandleTableView(database, new VBox(), handler, stage);
                    stage.setScene(tableView);

                }
                if (btn.getId().equals("addCSV")) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open CSV File");
                    fileChooser.getExtensionFilters()
                            .add(new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv"));
                    File file = fileChooser.showOpenDialog(AdminControls.this);

                    if (file != null) {
                        String path = file.getAbsolutePath();
                        insertDB(HandleCSV.readCSV(path));
                    } else {
                        LOGGER.log(Level.INFO, "Don't get Path to load csv");
                    }
                }

            }

        };
    }

    private void insertDB(ArrayList<String[]> list) {
        int counter = 0;
        for (String[] i : list) {
            if (checkAll(status, i[0], i[1])) {
                // check if this datarow already exists
                if (database.insertSimplyTable(status, i[0], i[1]) == false) {
                    LOGGER.log(Level.WARNING, "Can't insert Data to Database");
                    AlertClass.cantInsertAlert(i[0]);
                } else {
                    counter++;
                }
            } else {
                AlertClass.formatAlert();
            }

        }
        // if some data can't insert you get a alert
        // if no datarow can insert you wouldn't see that
        if (counter >= 1) {
            AlertClass.successInfoAlert();
        }

    }

    // check that no input is empty and check that input is a time with hh:mm
    public static boolean checkAll(String handler, String col1, String col2) {
        if (handler.equals("Movies")) {
            if (AdminControls.checkTime(col2) == true && !col1.equals("")) {
                return true;
            } else {
                return false;
            }
        }
        if (handler.equals("User")) {
            if (!col1.equals("") && !col2.equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean checkTime(String time) {
        boolean res = false;
        for (int i = 0; i < time.length(); i++) {
            char check = time.charAt(i);
            if (check >= 48 && check <= 57 && time.length() - 1 == 4) {
                if (i == 2 && check == 58) {
                    res = true;
                } else {
                    res = false;
                }
                res = true;
            } else {
                res = false;
            }
        }
        return res;
    }

    private TableView createOverview() {
        TableView table = new TableView<>();

        ObservableList<Information> list = FXCollections.observableArrayList();
        ResultSet rs;
        Information info;
        rs = database.selectQuery("Watch", false);
        try {
            while (rs.next()) {
                info = new Information(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5));
                list.add(info);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Can't get Data to show in tableview");
        }

        table.setItems(list);
        table.getColumns().addAll(setTableCols("user"), setTableCols("moviename"), setTableCols("date"),
                setTableCols("time"), setTableCols("seats"));
        return table;
    }

    public TableColumn<Information, String> setTableCols(String colname) {
        TableColumn<Information, String> col = new TableColumn<>(colname.toUpperCase());
        col.setCellValueFactory(new PropertyValueFactory<>(colname));
        return col;
    }

    private final Button exitButton = new Button("exit");
    private final Button logout = new Button("logout");
    private String status;
    private Stage stage;
    private Database database;

    public final static Logger LOGGER = Logger.getLogger(Menu.class.getName());

}