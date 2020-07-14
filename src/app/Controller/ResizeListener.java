package app.Controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.robot.Robot;
import java.lang.Number;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;


public class ResizeListener {
    public Scene scene;
    public Stage stage;
    public int currentWidth;
    public int currentHeight;
    public Boolean robot;


    public ResizeListener(Scene scene, Stage stage) {
        this.scene = scene;
        this.stage = stage;
        this.robot = false;
    }


    public void updateChanged (KeyEventHandler keyEventHandler) {
        getSceneWidth(keyEventHandler);
        getSceneHeight(keyEventHandler);
        robotKey();
    }

    public void robotKey () {
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
            final Timer timer = new Timer();
            TimerTask task = null;
            final long delayTime = 200;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {

                if (task != null) {
                    //System.out.println(task + "is cancelled.");
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Width: " + currentWidth);
                        // update UI ** RESIZE CODE //
                        keyEventHandler.textCenterX = newSceneWidth.intValue() / 2;
                        currentWidth = newSceneWidth.intValue();
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
            final long delayTime = 200;

            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                if (task != null) {
                    //System.out.println(task + "is cancelled.");
                    task.cancel();
                }

                task = new TimerTask() {
                    @Override
                    public void run() {
                        System.out.println("Height: " + currentHeight);
                        // update UI ** RESIZE CODE //
                        keyEventHandler.textCenterY = (newSceneHeight.intValue() - 25) / 2;
                        currentHeight = newSceneHeight.intValue();
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
