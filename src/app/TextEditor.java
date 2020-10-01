package app;

import app.Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnativehook.NativeHookException;

import java.io.IOException;


public class TextEditor extends Application {

    @Override
    public void start(Stage stage) throws IOException, NativeHookException {
        stage.setTitle("Simple Text Editor");

        /** Load View and Controller **/
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/app/View/UI.fxml"));
        MainController controller = new MainController();
        loader.setControllerFactory(t -> controller);

        /** Set Parent Root **/
        Parent root = loader.load();

        /** Set Stage and Scene and make it handle events (from the controller) **/
        Scene scene = new Scene(root);
        controller.setAll(stage, scene);
        controller.handleAll();

        /** Display the whole Scene (app) **/
        stage.setScene(scene);
        stage.setMinWidth(300);
        stage.setMinHeight(300);
        stage.show();

        /** Additional **/
        controller.handlePostLoading();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
