package app.Controller.Resize;

import app.Controller.KeyEventHandler;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.control.ScrollPane;
import javafx.scene.robot.Robot;
import java.lang.Number;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


public class ResizeListener {
    public Stage stage;
    public Scene scene;
    public ScrollPane textWindow;
    public KeyEventHandler keyEventHandler;

    public ResizeListener(Stage stage, Scene scene, KeyEventHandler keyEventHandler, ScrollPane textWindow) {
        this.stage = stage;
        this.scene = scene;
        this.textWindow = textWindow;
        this.keyEventHandler = keyEventHandler;
    }

    public void handleResizing() {
        ChangeListener<Number> changeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
                Platform.runLater(() -> {
                    keyEventHandler.handleText();
                    keyEventHandler.positioner.updatePosition();
                });
            }
        };
        scene.widthProperty().addListener(changeListener);
        scene.heightProperty().addListener(changeListener);
    }

    // Helper function for testing
    /*
    public void robotKey() {
        // Test function for keyInput
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyCode.F7);
            //System.out.println("Robot key pressed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


}
