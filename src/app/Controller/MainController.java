package app.Controller;

import app.Model.MenuMod;
import app.Model.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
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

    /** ---------- CLASS VARIABLES AND CONSTRUCTOR ---------- **/

    @FXML /** ROOT **/
    private BorderPane rootFXML;

    @FXML
    private AnchorPane anchor;

    @FXML
    private MenuBar menuFXML;

    private MenuMod model;
    private Scene scene;
    private Stage stage;

    /** >>> CONSTRUCTOR <<< **/
    public MainController(MenuMod model) {
        // To access additional methods in the model class
        this.model = model;
    }


    /** ---------- CLASS METHODS - PUBLIC ---------- **/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize is loaded last (access to loaded fxml), whereas constructor first (no fxml)
    }

    public void setAll(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    public void handleAll() {
        /** Create group that collects text to display **/
        Group group = new Group();

        /** Handle Key Events **/
        KeyEventHandler keyEventHandler = new KeyEventHandler(scene, group);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnKeyTyped(keyEventHandler);

        /** Handle Window Resize **/
        ResizeListener resizeListener = new ResizeListener(scene, stage);
        resizeListener.updateChanged(keyEventHandler);

        /** Handle Mouse Events **/
        MouseEventHandler mouseEventHandler = new MouseEventHandler(scene, stage, keyEventHandler);
        scene.setOnMousePressed(mouseEventHandler);
        scene.setOnMouseClicked(mouseEventHandler);
        scene.setOnMouseDragged(mouseEventHandler);
        scene.setOnMouseReleased(mouseEventHandler);

        /** Get screen properties **/
        // e.g. Rectangle2D [minX = 0.0, minY=0.0, maxX=1920.0, maxY=1080.0, width=1920.0, height=1080.0]
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        System.out.println(screenBounds);

        /** Text Nodes **/
        LinkedList ll = new LinkedList();
        //ll.insert('2');

        ll.showAll();

        /** Add group node to the root node to be displayed **/
        rootFXML.getChildren().add(group);
    }



    /** ---------- CLASS METHODS - PRIVATE ---------- **/
    @FXML
    private void onOpen() {
        model.openChosenFile(stage);
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
