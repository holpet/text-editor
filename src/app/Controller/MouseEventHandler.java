package app.Controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class MouseEventHandler implements EventHandler<MouseEvent>, NativeMouseInputListener {
    public Scene scene;
    public Stage stage;
    public KeyEventHandler keyEventHandler;
    public ResizeListener resizeListener;

    public MouseEventHandler(Scene scene, Stage stage, KeyEventHandler keyEventHandler) {
        this.scene = scene;
        this.stage = stage;
        this.keyEventHandler = keyEventHandler;
        this.resizeListener = new ResizeListener(scene);
    }


    @Override
    public void handle(MouseEvent mouseEvent) {
        System.out.println("Mouse pressed.");

        if (    mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

        }

    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        Platform.runLater(()->{
            resizeListener.robotKey();
        });
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
}
