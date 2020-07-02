package app;

import javafx.event.EventHandler;
import javafx.scene.control.MenuBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.File;


public class TE_Controller implements Initializable {

    /** ---------- CLASS VARIABLES AND CONSTRUCTOR ---------- **/

    @FXML /** ROOT **/
    private BorderPane rootFXML;

    @FXML
    private MenuBar menuFXML;

    private TE_Model model;
    private Scene scene;

    /** >>> CONSTRUCTOR <<< **/
    public TE_Controller(TE_Model model) {
        // To access additional methods in the model class
        this.model = model;
    }


    /** ---------- CLASS METHODS - PUBLIC ---------- **/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize is loaded last (access to loaded fxml), whereas constructor first (no fxml)
    }

    public void handleScene(Scene scene) {
        this.scene = scene;

        /** Create group that collects text to display **/
        Group group = new Group();
        int windowWidth = 800;
        int windowHeight = (600-25);

        /** Handle Key Events **/
        EventHandler<KeyEvent> keyEventHandler = new KeyEventHandler(group, windowWidth, windowHeight);
        this.scene.setOnKeyPressed(keyEventHandler);
        this.scene.setOnKeyTyped(keyEventHandler);

        /** Add group node to the root node to be displayed **/
        this.rootFXML.getChildren().add(group);

    }



    /** ---------- CLASS METHODS - PRIVATE ---------- **/
    @FXML
    private void onOpen() {
        //TO DO
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
