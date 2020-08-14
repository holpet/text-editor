package app.Controller;
import app.Model.Clipboard;
import app.Model.MyText;
import app.Model.Node;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Pair;

public class MouseEventHandler {
    private Clipboard clipboard;
    public KeyEventHandler keyEventHandler;
    public Positioner positioner;
    public HashMap<MyText, Node> hashMap;
    public Group group;
    public final ArrayList<Pair<Node, Boolean>> pairSelection;
    private Double deltaY;

    private EventHandler<MouseEvent> mouseMotionEventHandler;

    public MouseEventHandler(final Group group, KeyEventHandler keyEventHandler) {
        this.group = group;
        this.keyEventHandler = keyEventHandler;
        this.clipboard = keyEventHandler.textRenderer.textManipulator.clipboard;
        this.pairSelection = keyEventHandler.textRenderer.textManipulator.pairSelection;
        this.positioner = keyEventHandler.positioner;
        this.hashMap = keyEventHandler.hashMap;
        this.deltaY = 0.0;
        this.mouseMotionEventHandler = event -> {
            MouseEventHandler.this.doOnMouseMotion(event);
            event.consume();
        };
    }

    public void handleCursorSwitch() {
        group.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                keyEventHandler.scene.setCursor(Cursor.TEXT);
                group.setCursor(Cursor.TEXT);
            }
        });
        group.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                keyEventHandler.scene.setCursor(Cursor.DEFAULT);
                group.setCursor(Cursor.TEXT);
            }
        });
    }

    public void handleScroll() {
        keyEventHandler.textWindow.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldNum, Number newNum) {
                // count deltaY to adjust coordinates for findTargetText...
                Double height = keyEventHandler.textWindow.getContent().getLayoutBounds().getHeight();
                Double visAmount = keyEventHandler.textRenderer.textManipulator.getVisibleAmount();
                Double deltaY = ( (height-height*visAmount) * Math.abs(((Double)newNum-(Double)oldNum)));

                if ((Double)oldNum < (Double)newNum) setDeltaY(getDeltaY()+deltaY);
                else setDeltaY(getDeltaY()-deltaY);
                if (getDeltaY() < 0.5) setDeltaY(0.0);
            }
        });
    }

    private Boolean checkBoundaries(MyText target, Double x) {
        Double targetX = target.getX();
        Double targetWidth = target.getLayoutBounds().getWidth();
        return (x >= targetX && x <= (targetX + targetWidth));
    }

    public MyText findTargetText(MouseEvent event, String type) {
        int padding = 20;
        if (type.equals("drag")) padding = 0;
        double x = event.getX() - padding;
        double y = event.getY() + getDeltaY() - padding;

        //double y = event.getY() - padding;

        if (keyEventHandler.linkedText.isEmpty()) return null;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        //System.out.println("X: " + x + ", Y: " + y);

        // Determine what line did the user click on using y coord
        Integer letterHeight = (int)keyEventHandler.cursor.getSampleLetter().getLayoutBounds().getHeight();
        Integer lineNum = Math.floorDiv((int)y, letterHeight);
        if (lineNum > keyEventHandler.textRenderer.textManipulator.getLineCounter()) {
            lineNum = keyEventHandler.textRenderer.textManipulator.getLineCounter();
        }
        Node lineNode = keyEventHandler.hashMapIdx.get((lineNum)); // first node on a line

        // Traverse through line to find the closest x coord
        while (lineNode.getData().getY() == lineNum*letterHeight) {
            Boolean found = checkBoundaries(lineNode.getData(), x);
            if (found) {
                return lineNode.getData();
            }
            lineNode = lineNode.getNext();
            if ( (lineNode.getNext().getData().getY() != lineNum*letterHeight) || keyEventHandler.linkedText.isAtEnd(lineNode.getNext())) {
                return lineNode.getData();
            }
        }
        return null;
    }

    public EventHandler<MouseEvent> getMouseMotionEventHandler() {
        return mouseMotionEventHandler;
    }

    private void doOnMouseMotion(MouseEvent event) {

        EventType<? extends MouseEvent> eventType = event.getEventType();

        if (eventType == MouseEvent.MOUSE_PRESSED) {
            doOnMousePressed(event);
        }

        if (eventType == MouseEvent.MOUSE_DRAGGED) {
            doOnMouseDragged(event);
        }

        if (eventType == MouseEvent.MOUSE_RELEASED) {
            doOnMouseReleased(event);
        }
    }

    private void doOnMouseDragged(MouseEvent event) {
        makeSelection(event, "drag");
    }

    private void doOnMouseReleased(MouseEvent event) {
        makeSelection(event, "released");
    }

    private void doOnMousePressed(MouseEvent event) {
        MyText target = findTargetText(event, "pressed");
        clipboard.unselectAll();
        pairSelection.clear();

        // If user clicked on a letter (node):
        if (target != null) {
            // Determine which side of the letter user clicked.
            double mousePosX = event.getX()-20; // accounts for padding (20)
            double textPosX = target.getX();
            double halfWidth = target.getLayoutBounds().getWidth()/2;
            // If user clicked on the left side:
            if ( (mousePosX <= (textPosX+halfWidth)) && target.getX() != 0) {
                pairSelection.add(0, (new Pair<>(hashMap.get(target).getPrev(), positioner.getCursorIsAtStart())));
            }
            // If user clicked on the right side:
            else {
                pairSelection.add(0, (new Pair<>(hashMap.get(target), positioner.getCursorIsAtStart())));
            }
            positioner.updatePosition();
        }
    }

    private void makeSelection(MouseEvent event, String type) {
        MyText target = findTargetText(event, type);

        // If user clicked on a letter (node):
        if (target != null) {
            // Determine which side of the letter user clicked.
            double mousePosX = event.getX()-20; // accounts for padding (20)
            double textPosX = target.getX();
            double halfWidth = target.getLayoutBounds().getWidth()/2;
            // If user clicked on the left side:
            if ( mousePosX <= (textPosX+halfWidth)  ) {
                // and we are NOT at the beginning of the line
                if (target.getX() != 0) {
                    positioner.setCurrentNode(hashMap.get(target).getPrev());
                    positioner.setCursorIsAtStart(false);
                }
                else { // or we are at the beginning of the line
                    positioner.setCurrentNode(hashMap.get(target));
                    positioner.setCursorIsAtStart(true);
                }
            }
            // If user clicked on the right side:
            else {
                // current node stays, cursor is set after letter
                positioner.setCurrentNode(hashMap.get(target));
                positioner.setCursorIsAtStart(false);
            }
            positioner.updatePosition();

            pairSelection.add(1, (new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart())));
            keyEventHandler.textRenderer.textManipulator.moveSelection();
            //pairSelection.clear();
            //System.out.println("CurrentNode: " + positioner.getCurrentNode().getData().toString());
        }
    }

    public Double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(Double deltaY) {
        this.deltaY = deltaY;
    }
}
