package app.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.robot.Robot;
import java.lang.Number;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;


public class ResizeListener {
    public Scene scene;
    public int currentWidth;
    public int currentHeight;
    public Boolean robot;


    public ResizeListener(Scene scene) {
        this.scene = scene;
        this.robot = false;
    }


    public void updateChanged(KeyEventHandler keyEventHandler) {
        getSceneWidth(keyEventHandler);
        getSceneHeight(keyEventHandler);
        robotKey();
    }

    public void robotKey() {
        // Trigger a key event to handle text input based on new window size
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyCode.F7);
            //robot.keyType(KeyCode.SPACE);
            //robot.keyType(KeyCode.BACK_SPACE);
            //System.out.println("Robot key pressed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSceneWidth(KeyEventHandler keyEventHandler) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            final Timer timer = new Timer();
            TimerTask task = null;
            final long delayTime = 300;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, final Number newSceneWidth) {

                if (task != null) {
                    //System.out.println(task + "is cancelled.");
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        currentWidth = newSceneWidth.intValue();
                        System.out.println("Width: " + currentWidth);
                        // update UI ** RESIZE CODE //
                        keyEventHandler.currentWidth = newSceneWidth.intValue();
                        Platform.runLater(()->{
                            robotKey();
                        });
                    }
                };
                timer.schedule(task, delayTime);
            }
        });
    }

    private void getSceneHeight(KeyEventHandler keyEventHandler) {
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            final Timer timer = new Timer();
            TimerTask task = null;
            final long delayTime = 300;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, final Number newSceneHeight) {
                if (task != null) {
                    //System.out.println(task + "is cancelled.");
                    task.cancel();
                }

                task = new TimerTask() {
                    @Override
                    public void run() {
                        currentHeight = newSceneHeight.intValue();
                        System.out.println("Height: " + currentHeight);
                        // update UI ** RESIZE CODE //
                        keyEventHandler.currentHeight = newSceneHeight.intValue();
                        Platform.runLater(()->{
                            robotKey();
                        });
                    }
                };
                timer.schedule(task, delayTime);
            }
        });
    }

}
