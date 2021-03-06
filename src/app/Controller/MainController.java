package app.Controller;

import app.Controller.Menu.MenuHandler;
import app.Controller.Resize.ResizeListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class MainController implements Initializable {

    @FXML /** ROOT **/
    private BorderPane rootFXML;

    @FXML /** Scrollable space for text **/
    private ScrollPane textWindow;

    @FXML
    private MenuBar menuFXML;

    private MenuHandler menu;
    private Scene scene;
    private Stage stage;
    private KeyEventHandler keyEventHandler;

    public MainController() {
        // To access additional methods in the model class
    }


    /** ---------- CLASS METHODS - PUBLIC ---------- **/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public KeyEventHandler getKeyEventHandler() {
        return keyEventHandler;
    }

    public void setAll(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    public void handleAll() {
        /** Create group that collects text to display **/
        Group group = new Group();

        /** Handle Key Events **/
        this.keyEventHandler = new KeyEventHandler(stage, scene, group, textWindow);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyReleased(keyEventHandler);

        /** MENU **/
        this.menu = new MenuHandler(keyEventHandler);

        /** Handle Mouse Events **/
        MouseEventHandler mouseEventHandler = new MouseEventHandler(group, keyEventHandler);
        textWindow.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEventHandler.getMouseMotionEventHandler());
        textWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEventHandler.getMouseMotionEventHandler());
        group.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEventHandler.getMouseMotionEventHandler());
        mouseEventHandler.handleScroll();
        mouseEventHandler.handleCursorSwitch();

        /** Handle Window Resizing **/
        ResizeListener resizeListener = new ResizeListener(stage, scene, keyEventHandler, textWindow);
        resizeListener.handleResizing();

        /** Get screen properties **/
        // e.g. Rectangle2D [minX = 0.0, minY=0.0, maxX=1920.0, maxY=1080.0, width=1920.0, height=1080.0]
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(screenBounds);

        /** Add group node to be displayed **/
        //rootFXML.getChildren().add(group);
        textWindow.setContent(group);
    }

    public void handlePostLoading() {
        keyEventHandler.handleText();
        keyEventHandler.textRenderer.textManipulator.setScrollBar();
    }

    public void readTextFile() {
        keyEventHandler.handleText();
    }


    /** ---------- CLASS METHODS - PRIVATE ---------- **/
    @FXML
    private void onOpen() {
        menu.openChosenFile(stage);
        keyEventHandler.handleText();
    }

    @FXML
    private void onSave() {
        menu.writeTextToFile();
    }

    @FXML
    private void onSaveAs() {
        menu.saveToChosenFile(stage);
    }

    @FXML
    private void onClose() {
        System.exit(0);
    }

    @FXML
    private void onAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("About");
        alert.setContentText("This is a simple text editor app.");
        alert.show();
    }

}
