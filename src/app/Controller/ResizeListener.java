package app.Controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import java.lang.Number;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
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
        keyEventHandler.updateBBox();
    }

    private void getStageWidth() {
        ChangeListener changeListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldVal, Boolean newVal) {
                System.out.println("System iconified: " + newVal);
            }
        };
        stage.iconifiedProperty().addListener(changeListener);
    }


    private void getSceneWidth(KeyEventHandler keyEventHandler) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                keyEventHandler.textCenterX = newSceneWidth.intValue() / 2;
                currentWidth = newSceneWidth.intValue();
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
                System.out.println("Height: " + currentHeight);
            }
        });
    }

}
