package app.Controller;

import app.Model.Cursor;
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
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;


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
        this.menu = new MenuHandler();
    }


    /** ---------- CLASS METHODS - PUBLIC ---------- **/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize is loaded last (access to loaded fxml), whereas constructor first (no fxml)
        // Clear previous logging configurations.
        LogManager.getLogManager().reset();
        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
    }

    public void setAll(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    public void handleAll() {
        /** Create group that collects text to display **/
        Group group = new Group();

        /** Handle Key Events **/
        this.keyEventHandler = new KeyEventHandler(scene, group);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnKeyTyped(keyEventHandler);

        /** Handle Mouse Events **/
        try {
            GlobalScreen.registerNativeHook();
        }
        catch(NativeHookException e) {
            System.out.println("Native Mouse Hook couldn't be registered.");
            System.out.println(e.getMessage());
        }
        MouseEventHandler mouseEventHandler = new MouseEventHandler(scene, stage, keyEventHandler);
        scene.setOnMousePressed(mouseEventHandler);
        scene.setOnMouseClicked(mouseEventHandler);
        scene.setOnMouseDragged(mouseEventHandler);
        scene.setOnMouseReleased(mouseEventHandler);
        GlobalScreen.addNativeMouseListener(mouseEventHandler);
        GlobalScreen.addNativeMouseMotionListener(mouseEventHandler);

        SelectionHandler selectionHandler = new SelectionHandler(group, keyEventHandler);
        textWindow.addEventHandler(MouseEvent.MOUSE_PRESSED, selectionHandler.getMousePressedEventHandler());


        /** Handle Window Resize **/
        /**
        ResizeListener resizeListener = new ResizeListener(scene);
        resizeListener.updateChanged(keyEventHandler);
         **/

        /** Get screen properties **/
        // e.g. Rectangle2D [minX = 0.0, minY=0.0, maxX=1920.0, maxY=1080.0, width=1920.0, height=1080.0]
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(screenBounds);


        /** Add group node to be displayed **/
        //rootFXML.getChildren().add(group);
        textWindow.setContent(group);
    }

    public void readTextFile() {
        keyEventHandler.handleText();
    }


    /** ---------- CLASS METHODS - PRIVATE ---------- **/
    @FXML
    private void onOpen() {
        menu.openChosenFile(stage);
    }

    @FXML
    private void onSave() {
        /**
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            IOResult<TextFile> io = model.load(file.toPath());
            if (io.isOK) {
            }
        }**/
    }

    @FXML
    private void onSaveAs() {
        //TO DO
    }

    @FXML
    private void onClose() {
        /** model.close(); **/
        System.exit(0);
    }

    @FXML
    private void onDelete() {
        //TO DO
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
