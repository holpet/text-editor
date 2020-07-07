package app.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.robot.Robot;
import java.lang.Number;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ResizeListener {
    public Scene scene;
    public Stage stage;
    public int currentWidth;
    public int currentHeight;


    public ResizeListener(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
    }


    public void updateChanged (KeyEventHandler keyEventHandler) {
        getSceneWidth(keyEventHandler);
        getSceneHeight(keyEventHandler);
    }

    private void robotKey () {
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyCode.F1);
            robot.keyType(KeyCode.F1);
            //System.out.println("Robot key pressed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSceneWidth(KeyEventHandler keyEventHandler) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                keyEventHandler.textCenterX = newSceneWidth.intValue() / 2;
                currentWidth = newSceneWidth.intValue();
                robotKey();
                System.out.println("Width: " + currentWidth);
            }
        });
    }

    private void getSceneHeight(KeyEventHandler keyEventHandler) {
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                currentHeight = newSceneHeight.intValue();
                keyEventHandler.textCenterY = (newSceneHeight.intValue() - 25) / 2;
                robotKey();
                System.out.println("Height: " + currentHeight);
            }
        });
    }

}
