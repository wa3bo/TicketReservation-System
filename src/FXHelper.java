package reservations;

import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.TextField;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Tab;

public class FXHelper {

    // setup stage
    public static void prepStage(Stage stage, int minW, int maxW, int minH, int maxH) {
        stage.setMinWidth(minW);
        stage.setMinHeight(minH);
        stage.setMaxHeight(maxH);
        stage.setMaxWidth(maxW);

    }

    // for seats legend (Menu)
    public static VBox getVBoxes(VBox box, String text) {
        VBox innBox = box;
        Label labelText = new Label(text);
        labelText.setTextAlignment(TextAlignment.CENTER);
        innBox.getChildren().add(labelText);
        innBox.setAlignment(Pos.CENTER);
        innBox.setSpacing(5);
        return innBox;
    }

    // legend menu
    public static HBox createHBoxMenu(VBox box1, VBox box2, VBox box3, VBox box4) {
        HBox box = createHBox();
        box.getChildren().addAll(box1, box2, box3, box4);
        box.setSpacing(20);
        box.setAlignment(Pos.BASELINE_CENTER);
        return box;
    }

    // for buttons with image
    public static ImageView setUpImageView(Image image) {
        ImageView view = new ImageView(image);
        view.setFitHeight(20);
        view.setFitWidth(20);
        return view;
    }

    // Hbox for Summary
    public static HBox createHboxSummary(Button btn, Button btn2) {
        HBox box = createHBox();
        box.getChildren().addAll(btn, btn2);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    // HBox for tableView
    public static HBox createHBoxTable(TextField col1, TextField col2, Button btn, VBox vbox) {
        HBox box = createHBox();
        box.getChildren().addAll(col1, col2, btn, vbox);
        return box;
    }

    public static HBox createHBox() {
        HBox box = new HBox();
        box.setSpacing(10);
        box.setPadding(new Insets(10, 10, 10, 10));
        return box;
    }

    // for tab
    public static Tab createTab(String text, VBox box) {
        Tab tab = new Tab();
        tab.setText(text);
        box.setAlignment(Pos.CENTER);
        tab.setContent(box);
        tab.setClosable(false);
        return tab;
    }

    public static final KeyCombination exitComb = new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination infoComb = new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination bookComb = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
    public static final KeyCombination histComb = new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN);

}