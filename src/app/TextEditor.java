package app;

import app.Controller.TE_Controller;
import app.Model.TE_Model;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class TextEditor extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Single Letter Display");

        /** Load View and Controller **/
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/View/TE_View.fxml"));
        TE_Controller controller = new TE_Controller(new TE_Model());
        loader.setControllerFactory(t -> controller);

        /** Set Parent Root **/
        Parent root = loader.load();

        /** Set Stage and Scene and make it handle events (from the controller) **/
        Scene scene = new Scene(root);
        controller.setAll(stage, scene);
        controller.handleAll();

        /** Display the whole Scene (app) **/
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
