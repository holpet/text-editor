package app.Controller;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MouseEventHandler implements EventHandler<MouseEvent> {
    public Scene scene;
    public Stage stage;
    public KeyEventHandler keyEventHandler;

    public MouseEventHandler(Scene scene, Stage stage, KeyEventHandler keyEventHandler) {
        this.scene = scene;
        this.stage = stage;
        this.keyEventHandler = keyEventHandler;
    }


    @Override
    public void handle(MouseEvent mouseEvent) {
        //System.out.println("Mouse pressed.");

        if (    mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED ||
                mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

            //keyEventHandler.positioner.updateCursor();

        }

    }
}
