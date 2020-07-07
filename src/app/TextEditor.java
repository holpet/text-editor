package app;

import app.Controller.MainController;
import app.Model.MenuModel;
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
        loader.setLocation(getClass().getResource("/app/View/UI.fxml"));
        MainController controller = new MainController(new MenuModel());
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
