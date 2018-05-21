package reservations;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.ResultSet;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class Menu extends Stage {

	public Menu(BorderPane borderPane, Database database) {

		LOGGER.addHandler(Log.fileHandler);
		LOGGER.setLevel(Level.ALL);

		this.database = database;
		user = Login.loggedInUser;

		hBoxTop.setAlignment(Pos.CENTER_RIGHT);
		vBoxSide.setAlignment(Pos.CENTER);
		vBoxSide.setPadding(new Insets(0, 10, 0, 10));
		hBoxTop.setPadding(new Insets(0, 10, 0, 10));

		final EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {

			public void handle(ActionEvent e) {

				// go back to login
				if (e.getSource() == logoutButton) {
					logout();
				}
				if (e.getSource() == exitButton) {
					LOGGER.log(Level.INFO, "Exit Application");
					Menu.this.close();
				}

				if (e.getSource() == historyButton) {
					HandleTableView tableView = new HandleTableView(database, new VBox(), "Watch", Menu.this);
					LOGGER.log(Level.INFO, "View History");		
					Menu.this.setScene(tableView);
				}

				if (e.getSource() == bookButton) {
					// book seat now
					bookNow();
				}

			}
		};

		// add movies in MenuButton
		getmovieMenuItems(movieChoice);
		movieChoice.getItems().addAll(movieMenuItems);

		setMovieItems();

		bookButton.setMinWidth(200);
		historyButton.setMinWidth(200);

		movieChoice.setMinWidth(200);
		movieChoice.setAlignment(Pos.CENTER);
		selectDate.setMinWidth(200);
		selectTime.setAlignment(Pos.CENTER);
		selectTime.setMinWidth(200);

		hBoxTop.getChildren().addAll(new Text(user), logoutButton, exitButton);
		vBoxSide.getChildren().addAll(selectDate, movieChoice, selectTime, historyButton, bookButton);

		seatsBox.getChildren().add(createSeats());

		seatsBox.setId("centerBox");
		seatsBox.setAlignment(Pos.BOTTOM_CENTER);

		borderPane.setTop(hBoxTop);
		borderPane.setLeft(vBoxSide);
		borderPane.setCenter(seatsBox);

		logoutButton.setOnAction(eventHandler);
		exitButton.setOnAction(eventHandler);
		historyButton.setOnAction(eventHandler);
		bookButton.setOnAction(eventHandler);

		Scene scene = new Scene(borderPane, 800, 400);
		// this.setResizable(false);
		scene.getStylesheets().add("File:../ressources/stylesheet.css");
		hBoxTop.getStyleClass().add("hbox");
		vBoxSide.getStyleClass().add("vbox");

		this.setTitle("Reservation Menu");
		this.setScene(scene);
		FXHelper.prepStage(this, 800, 1000, 450, 450);
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
				if (FXHelper.bookComb.match(key)) {
					bookNow();
				}
				if (FXHelper.histComb.match(key)) {
					HandleTableView tableView = new HandleTableView(database, new VBox(), "Watch", Menu.this);
					Menu.this.setScene(tableView);
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

		Menu.this.close();
	}

	private void getmovieMenuItems(MenuButton button) {
		MenuItem item;

		ResultSet rs = database.selectQuery("Movies", true);
		LOGGER.log(Level.INFO, "Get Data from Movie Table for Movie");

		try {
			while (rs.next()) {
				item = new MenuItem(rs.getString(1));
				// 1 -> col index
				item.setId(rs.getString(1));
				movieMenuItems.add(item);
				// button.getItems().add(item);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't get Data from Movie Table for Movie");

		}
	}

	private void getTimeItems(MenuButton button) {
		MenuItem item;

		ResultSet rs = database.selectQuery("Movies", movieChoice.getText());
		// LOGGER.log(Level.INFO, "Get Data from Movie Table for Time");
		timeMenuItems.clear();

		try {
			while (rs.next()) {
				item = new MenuItem(rs.getString(1));
				// 1 -> col index
				item.setId(rs.getString(1));
				timeMenuItems.add(item);
				// button.getItems().add(item);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't get Data from Movie Table for Time");

		}
	}

	private GridPane createGridSeats(GridPane grid) {
		grid.setPadding(new Insets(10, 40, 10, 40));
		grid.setVgap(8);
		grid.setHgap(10);

		char buchstabe = 'A';

		for (int i = 0; i < buttonArray.length; i++) {
			for (int j = 0; j < buttonArray[0].length; j++) {
				if (i == 0) {
					grid.add(new Text(Character.toString(buchstabe)), i, j);
					buchstabe++;
				}

				else if (i == 4) {
					Button btn = new Button();
					btn.setPrefWidth(50);
					btn.setPrefHeight(50);
					grid.add(btn, i, j);
					btn.setId("transButton");

				}

				else {
					Button btn = new Button();
					btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
					btn.setMaxHeight(500);
					btn.setOnAction(handleSelectBtn(btn));
					btn.setGraphic(new ImageView(imageFree));
					// on some point later i can differentiate the buttons
					btn.setId("free");
					buttonArray[i][j] = btn;
					grid.add(btn, i, j);
					GridPane.setVgrow(btn, Priority.ALWAYS);
					GridPane.setHgrow(btn, Priority.ALWAYS);
				}

			}
		}

		return grid;
	}

	private BorderPane createSeats() {

		VBox seatBox_ = new VBox();
		HBox legend = new HBox();
		HBox infoBox = new HBox();
		VBox legendFree = new VBox();
		VBox legendSelect = new VBox();
		VBox legendBooked = new VBox();
		VBox legendOwnBookings = new VBox();
		BorderPane pane = new BorderPane();
		GridPane grid = new GridPane();
		grid = createGridSeats(grid);

		LOGGER.log(Level.INFO, "Create seats for gridPane");

		VBox box = new VBox();
		box.getChildren().add(grid);

		seatBox_.getChildren().add(box);

		legendBooked.getChildren().add(new ImageView(imageBooked));
		FXHelper.getVBoxes(legendBooked, "Booked");

		legendOwnBookings.getChildren().add(new ImageView(imageYourBooking));
		FXHelper.getVBoxes(legendOwnBookings, "Your Bookings");

		legendSelect.getChildren().add(new ImageView(imageSelect));
		FXHelper.getVBoxes(legendSelect, "Selected");

		legendFree.getChildren().add(new ImageView(imageFree));
		FXHelper.getVBoxes(legendFree, "Avaliable");

		legend = FXHelper.createHBoxMenu(legendBooked, legendOwnBookings, legendFree, legendSelect);

		col1.setText(String.format("booked Seats: %s", seatCounter));
		col2.setText(String.format("avaliable Seats: %s", 18 - seatCounter));
		col3.setText(String.format("total Seats: 18"));

		HBox box1 = FXHelper.createHBox();
		box1.getChildren().add(col1);
		box1.setAlignment(Pos.BASELINE_LEFT);

		HBox box2 = FXHelper.createHBox();
		box2.getChildren().add(col2);
		box2.setAlignment(Pos.BASELINE_CENTER);

		HBox box3 = FXHelper.createHBox();
		box3.getChildren().add(col3);
		box3.setAlignment(Pos.BASELINE_RIGHT);

		infoBox = FXHelper.createHBox();
		infoBox.getChildren().addAll(box1, box2, box3);
		infoBox.setAlignment(Pos.CENTER);
		seatBox_.getChildren().add(infoBox);

		pane.setCenter(seatBox_);
		pane.setTop(legend);
		// pane.setBottom(infoBox);
		return pane;
	}

	// remove old and add new items
	private void setMovieItems() {
		for (MenuItem item : movieMenuItems) {
			item.setOnAction(handleActions(item, movieChoice));
		}

	}

	// remove old and add new items
	private void setTimeItems() {
		for (MenuItem item : timeMenuItems) {
			item.setOnAction(handleActions(item, selectTime));
		}
	}

	// Handle MenuButton to Select Item
	private EventHandler<ActionEvent> handleActions(MenuItem item, MenuButton btn) {
		return new EventHandler<ActionEvent>() {

			// change time for every time movie has changed
			public void handle(ActionEvent event) {
				// to reset buttons
				clearSeats();

				btn.setText(item.getId());
				if (btn == movieChoice) {
					selectTime.setText("Select Time");

				}

				// add movieItems for new MovieChoice
				selectTime.getItems().removeAll(timeMenuItems);
				getTimeItems(selectTime);
				selectTime.getItems().addAll(timeMenuItems);
				setTimeItems();
				if (checkAll() == true) {
					getSeats(movieChoice.getText(), selectDate.getValue().toString(), selectTime.getText());

					// to update values from this labels
					col1.setText(String.format("booked Seats: %s", seatCounter));
					col2.setText(String.format("avaliable Seats: %s", 18 - seatCounter));
					col3.setText(String.format("total Seats: 18"));
				}

			}

		};
	}

	private EventHandler<ActionEvent> handleSelectBtn(Button btn) {
		return new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if (checkAll() == true) {

					if (btn.getId().equals("selected")) {
						btn.setGraphic(new ImageView(imageFree));
						btn.setId("free");
					} else if (btn.getId().equals("free")) {
						btn.setGraphic(new ImageView(imageSelect));
						btn.setId("selected");
					}
				}
			}

		};
	}

	private boolean checkAll() {
		String moviename = movieChoice.getText();
		// String date = selectDate.getValue().toString();
		String time = selectTime.getText();
		if (selectDate.getValue() != null && !time.equals("Select Time") && !moviename.equals("Select Movie")) {
			return true;
		} else {
			return false;
		}
	}

	private void getSeats(String moviename, String date, String time) {

		ResultSet seats = database.getSeatsFromWatch(moviename, date, time);
		try {
			while (seats.next()) {
				String seatnr = seats.getString(1);
				String user = seats.getString(2);
				setSets(seatnr, user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Error occured");
		}
	}

	// set in gridpane
	private void setSets(String seatnr, String user) {

		for (int i = 0; i < seatnr.length() - 1; i++) {
			if (seatnr.charAt(i) >= 49 && seatnr.charAt(i) <= 56 && seatnr.charAt(i + 1) != ';') {
				int setx = Character.getNumericValue(seatnr.charAt(i));
				int sety = Character.getNumericValue(seatnr.charAt(i + 1));
				buttonArray[setx][sety].setId("booked");
				seatCounter++;
				// your booking
				if (user.equals(this.user)) {
					buttonArray[setx][sety].setGraphic(new ImageView(imageYourBooking));
				} else {
					buttonArray[setx][sety].setGraphic(new ImageView(imageBooked));
				}
			}
			if (seatnr.charAt(i) == ';') {
				continue;
			}
		}
	}

	// to reset seats if anything is changed
	private void clearSeats() {
		seatCounter = 0;
		for (int i = 1; i < buttonArray.length; i++) {
			for (int j = 0; j < buttonArray[0].length; j++) {
				if (i == 4)
					continue;
				buttonArray[i][j].setGraphic(new ImageView(imageFree));
				buttonArray[i][j].setId("free");
			}
		}
	}

	private void bookNow() {
		StringBuilder store = new StringBuilder();
		StringBuilder res = new StringBuilder();
		if (checkAll() == true) {
			for (int i = 1; i < buttonArray.length; i++) {
				for (int j = 0; j < buttonArray[0].length; j++) {
					if (i == 4)
						continue;
					if (buttonArray[i][j].getId().equals("selected")) {
						String setx = Integer.toString(i);
						String sety = Integer.toString(j);
						store.append(setx);
						store.append(sety);
						buttonArray[i][j].setId("booked");
					}
				}
			}

			// ceck if a seat is selected
			if (!store.toString().equals("")) {
				for (int i = 0; i < store.length(); i++) {
					res.append(store.charAt(i));
					// add ; to handle them in db and in the code again
					if (i % 2 == 1 && i != 0 && i != store.length() - 1) {
						res.append(";");
					}
				}
				database.insertWatch(user, movieChoice.getText(), selectDate.getValue().toString(),
						selectTime.getText(), res.toString());
				summary = new Information(user, movieChoice.getText(), selectDate.getValue().toString(),
						selectTime.getText(), res.toString());

				new Summary(new VBox(), summary, database);
				LOGGER.log(Level.INFO, "Go to Summary with informations");
				Menu.this.close();

			} else {
				AlertClass.seatAlert();
			}

		}

	}

	private final Image imageFree = new Image("File:../ressources/fonts-images/blackSeat.png");
	private final Image imageBooked = new Image("File:../ressources/fonts-images/whiteSeat.png");
	private final Image imageSelect = new Image("File:../ressources/fonts-images/redSeat.png");
	private final Image imageYourBooking = new Image("File:../ressources/fonts-images/greySeat.png");

	private final Button logoutButton = new Button("Logout");
	private final Button exitButton = new Button("Exit");
	private final Button historyButton = new Button("History",
			FXHelper.setUpImageView(new Image("File:../ressources/fonts-images/history.png")));
	private final Button bookButton = new Button("Book", FXHelper.setUpImageView(imageFree));

	private HBox hBoxTop = new HBox(10);
	private VBox vBoxSide = new VBox(30);
	private String user;
	private int seatCounter = 0; // avaliable seats

	private HBox seatsBox = new HBox(100);

	private Button[][] buttonArray = new Button[8][3];

	private MenuButton movieChoice = new MenuButton("Select Movie");
	private DatePicker selectDate = new DatePicker();
	private MenuButton selectTime = new MenuButton("Select Time");

	private Database database;

	// for menuItems
	private ArrayList<MenuItem> movieMenuItems = new ArrayList<MenuItem>();
	private ArrayList<MenuItem> timeMenuItems = new ArrayList<MenuItem>();

	// to show the reservations in the summary
	private Information summary;

	// legend on the bottom with avaliable seat, and booked seats and total seats
	Label col1 = new Label();
	Label col2 = new Label();
	Label col3 = new Label();

	public final static Logger LOGGER = Logger.getLogger(Menu.class.getName());

}