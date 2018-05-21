package reservations;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import reservations.AdminControls;
import reservations.AlertClass;
import reservations.Database;
import reservations.FXHelper;
import reservations.Information;

import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class HandleTableView extends Scene {

    public HandleTableView(Database database, VBox vbox, String handler, Stage stage) {
        super(vbox, 500, 400);

        this.database = database;
        this.handler = handler;
        this.stage = stage;

        FXHelper.prepStage(stage, 500, 650, 400, 500);
        stage.setWidth(500);

        LOGGER.addHandler(Log.fileHandler);
        LOGGER.setLevel(Level.ALL);

        table = new TableView<>();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setItems(getData(handler));
        vbox.getChildren().add(table);
        // differentiate if it is a simply table or table watch
        if (!handler.equals("Watch")) {
            if (handler.equals("User")) {
                LOGGER.log(Level.INFO, "tableview user");
                table.getColumns().addAll(setTableCols("user"), setTableCols("password"));
            }
            if (handler.equals("Movies")) {
                LOGGER.log(Level.INFO, "tableview movies");
                table.getColumns().addAll(setTableCols("moviename"), setTableCols("time"));
            }
            vbox.getChildren().add(createBottomArea(handler));
        } else if (handler.equals("Watch")) {
            stage.setTitle("History");
            table.getColumns().addAll(setTableCols("user"), setTableCols("moviename"), setTableCols("date"),
                    setTableCols("time"), setTableCols("seats"));
            vbox.getChildren().add(delBack());

        } else {
            LOGGER.log(Level.SEVERE, "wrong table");
        }

        this.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ESCAPE)) {
                    goBack();
                }
                if (FXHelper.exitComb.match(key)) {
                    System.exit(0);
                }
            }
        });

    }

    private ObservableList<Information> getData(String handler) {
        ObservableList<Information> list = FXCollections.observableArrayList();
        ResultSet rs;
        Information info;
        if (handler.equals("Watch")) {
            rs = database.selectQueryWatch(Login.loggedInUser);
        } else {
            rs = database.selectQuery(handler, false);
        }

        try {
            int counter = 0;
            while (rs.next()) {
                if (handler.equals("Watch")) {
                    info = new Information(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getString(5));
                } else {
                    info = new Information(handler, rs.getString(1), rs.getString(2));
                }
                list.add(info);
                counter++;
            }
            return list;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Don't get Data in observable list");
            return null;
        }

    }

    // to set the column in the tableview
    private TableColumn<Information, String> setTableCols(String colname) {
        TableColumn<Information, String> col = new TableColumn<>(colname.toUpperCase());
        // colname is property from the information class
        col.setCellValueFactory(new PropertyValueFactory<>(colname)); // info: if var = "name"; getter hat to named
                                                                      // "getName" !!!
        return col;
    }

    private HBox createBottomArea(String handler) {

        if (handler.equals("User")) {
            col1.setPromptText("Name");
            col2.setPromptText("Password");
        }
        if (handler.equals("Movies")) {
            col1.setPromptText("Moviename");
            col2.setPromptText("Time");
        }

        // new writing e ->
        addButton.setOnAction(e -> addData());
        pressKey(addButton);

        HBox hbox = FXHelper.createHBoxTable(col1, col2, addButton, delBack());
        return hbox;
    }

    // create delete and back button here, to use it also for table Watch
    private VBox delBack() {
        VBox vbox = new VBox();
        deleteButton.setOnAction(e -> deleteData());
        backButton.setOnAction(e -> goBack());
        pressKey(deleteButton);
        pressKey(backButton);
        if (!handler.equals("Watch")) {
            vbox.setPadding(new Insets(0, 10, 10, 0));
        } else {
            vbox.setPadding(new Insets(10, 10, 10, 10));
        }

        vbox.setSpacing(10);
        vbox.getChildren().addAll(deleteButton, backButton);
        return vbox;
    }

    private void addData() {
        Information colInfo = new Information(handler, col1.getText(), col2.getText());

        if (AdminControls.checkAll(handler, col1.getText(), col2.getText())) {
            // check if this datarow already exists
            if (database.insertSimplyTable(handler, col1.getText(), col2.getText()) == false) {
                LOGGER.log(Level.WARNING, "Can't insert Data to Database");
                AlertClass.cantInsertAlert(col1.getText());
            } else {
                LOGGER.log(Level.INFO, "Insert successful");
                table.getItems().add(colInfo);
            }
        } else {
            AlertClass.formatAlert();
        }

        col1.clear();
        col2.clear();
    }

    private void deleteData() {
        ObservableList<Information> delete;
        delete = table.getSelectionModel().getSelectedItems();

        // remove selected data from database
        if (handler.equals("User")) {
            for (Information info : delete) {
                // can't delete an admin
                if (info.getUser().equals("admin")) {
                    AlertClass.deleteAdmin();
                } else {
                    database.deleteSimplyTable(handler, info.getUser(), info.getPassword());
                    LOGGER.log(Level.INFO, "delete user successful");
                }
            }

        } else if (handler.equals("Movies")) {
            for (Information info : delete) {
                database.deleteSimplyTable(handler, info.getMoviename(), info.getTime());
                database.deleteWatchMovie(info.getMoviename(), info.getTime());
                LOGGER.log(Level.INFO, "delete movie successful");
            }

        } else if (handler.equals("Watch")) {
            for (Information info : delete) {
                // ystem.out.println(info.getUser()+ info.getMoviename()+ info.getDate()+
                // info.getTime()+ info.getSeats());
                database.deleteWatch(info.getUser(), info.getMoviename(), info.getDate(), info.getTime(),
                        info.getSeats());
                LOGGER.log(Level.INFO, "delete reservation successful");
            }
        } else {
            LOGGER.log(Level.SEVERE, "Can't delete data");
        }
        // reload tableview
        table.setItems(getData(handler));

    }

    private void goBack() {
        if (!handler.equals("Watch")) {
            new AdminControls(new HBox(), database);
            stage.close();
        } else {
            new Menu(new BorderPane(), database);
            stage.close();
        }

    }

    // add with enter
    private void pressKey(Button button) {
        button.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent key) {
                if (key.getCode().equals(KeyCode.ENTER)) {
                    // addData();
                    if (button == addButton) {
                        addData();
                    }
                    if (button == deleteButton) {
                        deleteData();
                    }
                    if (button == backButton) {
                        goBack();
                    }
                }
            }

        });
    }

    private Database database;
    private TableView<Information> table;
    private TextField col1 = new TextField();
    private TextField col2 = new TextField();
    private String handler;
    private Stage stage;
    private Button deleteButton = new Button("delete");
    private Button backButton = new Button("back");
    private Button addButton = new Button("add");

    public final static Logger LOGGER = Logger.getLogger(Menu.class.getName());

}