package app;

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
        loader.setLocation(getClass().getResource("TE_View.fxml"));
        TE_Controller controller = new TE_Controller(new TE_Model());
        loader.setControllerFactory(t -> controller);

        /** Set Parent Root **/
        Parent root = loader.load();

        /** Set Scene and make it handle events (from the controller) **/
        Scene scene = new Scene(root);
        controller.handleScene(scene);

        /** Display the whole Scene (app) **/
        stage.setScene(scene);
        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
