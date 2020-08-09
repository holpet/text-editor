package app.Controller;
import app.Model.Clipboard;
import app.Model.MyText;
import app.Model.Node;
import app.Model.SelectableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static javafx.scene.paint.Color.rgb;

public class SelectionHandler {
    private Clipboard clipboard;
    public KeyEventHandler keyEventHandler;
    public Positioner positioner;
    public HashMap<MyText, Node> hashMap;
    public Group group;

    private EventHandler<MouseEvent> mousePressedEventHandler;
    private EventHandler<MouseDragEvent> mouseDraggedEventHandler;

    public SelectionHandler(final Group group, KeyEventHandler keyEventHandler) {
        this.group = group;
        this.keyEventHandler = keyEventHandler;
        this.clipboard = new Clipboard();
        this.positioner = keyEventHandler.positioner;
        this.hashMap = keyEventHandler.hashMap;
        this.mousePressedEventHandler = event -> {
            SelectionHandler.this.doOnMousePressed(event);
            event.consume();
        };
        this.mouseDraggedEventHandler = event -> {
            SelectionHandler.this.doOnMouseDragged(event);
            event.consume();
        };
    }

    public Node findClosestNode(MouseEvent event) {
        // click on scrollpane
        // determine id user clicked on a node
        // if not, find the closest node (first x, then y axis)
        double x = event.getX();
        double y = event.getY();
        System.out.println("X: " + x + ", Y: " + y);
        Optional<javafx.scene.Node> found = group.getChildren().stream().filter(n -> {
            Bounds bounds = n.getBoundsInLocal();
            Point2D point = new Point2D(x, y);
            System.out.println("Point x: " + point.getX() + ", point Y: " + point.getY());
            return n.contains(point.getX(), point.getY());
        }).findAny();
        MyText text = (MyText) found.get();
        Node node = hashMap.get(text);
        if (node == null) {
            System.out.println("No node found. Find the closest...");

        }
        else {
            System.out.println("Node found: " + node.getData());
        }
        return node;
    }

    public EventHandler<MouseDragEvent> getMouseDraggedEventHandler() {
        return mouseDraggedEventHandler;
    }

    private void doOnMouseDragged(MouseDragEvent event) {
        Node start = null;
        Node end = null;

        EventType<MouseDragEvent> eventType = event.getEventType();
        if (eventType == MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET) {
            MyText targetStart;
            try {
                targetStart = (MyText) event.getTarget();
                targetStart.getStyleableNode();
                System.out.println("Target Start is MyText.");
                start = hashMap.get(targetStart);
            }
            catch (Exception e) {
                targetStart = null;
                System.out.println(e);
                clipboard.unselectAll();
                return;
            }
        }
        if (eventType == MouseDragEvent.MOUSE_DRAG_EXITED_TARGET) {
            MyText targetEnd;
            try {
                targetEnd = (MyText) event.getTarget();
                targetEnd.getStyleableNode();
                System.out.println("Target End is MyText.");
                end = hashMap.get(targetEnd);
            }
            catch (Exception e) {
                targetEnd = null;
                System.out.println(e);
                clipboard.unselectAll();
                return;
            }
        }
        if (start == null || end == null) return;
        Node tmp = start;
        while(tmp != end.getNext()) {
            if(group.getChildren().contains(tmp.getData())) {
                if(!clipboard.getSelectedItems().contains(tmp.getData()))
                    clipboard.unselectAll();
                clipboard.select(tmp.getData(), true);
            }
            tmp = tmp.getNext();
        }

    }

    public EventHandler<MouseEvent> getMousePressedEventHandler() {
        return mousePressedEventHandler;
    }

    private void doOnMousePressed(MouseEvent event) {
        //findClosestNode(event);
        MyText target_myText;
        try {
            target_myText = (MyText) event.getTarget();
            target_myText.getStyleableNode();
            System.out.println("Target is MyText.");
        }
        catch (Exception e) {
            target_myText = null;
            System.out.println(e);
        }

        // If user clicked on a letter:
        if (target_myText != null) {

            System.out.println(hashMap.get(target_myText).getData().toString());

            // Determine which side of the letter user clicked.
            double mousePosX = event.getX()-20; // accounts for padding (20)
            double textPosX = target_myText.getX();
            double halfWidth = target_myText.getLayoutBounds().getWidth()/2;
            // If user clicked on the left side:
            if ( mousePosX <= (textPosX+halfWidth)  ) {
                // and we are NOT at the beginning of the text
                if (!positioner.linkedText.isAtBeginning(hashMap.get(target_myText).getPrev())) {
                    // current node == current node.prev and cursor is set after letter
                    positioner.setCurrentNode(hashMap.get(target_myText).getPrev());
                    positioner.setCursorIsAtStart(false);
                }
                else {
                    System.out.println("Cursor at start...");
                    positioner.setCurrentNode(hashMap.get(target_myText));
                    positioner.setCursorIsAtStart(true);
                }
            }
            // If user clicked on the right side:
            else {
                // current node stays, cursor is set after letter
                positioner.setCurrentNode(hashMap.get(target_myText));
                positioner.setCursorIsAtStart(false);
            }
            positioner.updatePosition();
            System.out.println("CurrentNode: " + positioner.getCurrentNode().getData().toString());

            //setBackgroundColors();
            /**
            if(group.getChildren().contains(target_myText)) {
                if(!clipboard.getSelectedItems().contains(target_myText))
                    clipboard.unselectAll();
                clipboard.select(target_myText, true);
            }**/
        }
        // If user didn't click on a letter
        else {
            System.out.println("Target equals parent.");
            //clipboard.unselectAll();
        }
    }

    private void setBackgroundColors(){
        //Bind property to text
        //final Bounds out = keyEventHandler.textWindow.getContent().getBoundsInLocal();
        //final StringBuilder sbColors = new StringBuilder();
        //final StringBuilder sbInsets = new StringBuilder();
        //AtomicInteger cont = new AtomicInteger();
        // for each selected letter in linked list:
        //sbColors.append("rgb(110, 173, 196)");

        MyText letter = positioner.getCurrentNode().getData();
        double x = letter.getBoundsInParent().getMinX();
        double y = letter.getBoundsInParent().getMinY();
        double width = letter.getLayoutBounds().getWidth();
        double height = letter.getLayoutBounds().getHeight();
        Rectangle background = new Rectangle(x, y, width, height);

        background.setFill(rgb(110, 173, 196));
        keyEventHandler.group.getChildren().add(background);
        background.toBack();

        /**
        keyEventHandler.group.getChildren().forEach (letter -> {
            Bounds bounds = letter.getBoundsInParent();
            sbInsets.append(bounds.getMinY()).append(" ");
            sbInsets.append(Math.min(keyEventHandler.scene.getWidth(),out.getMaxX())-bounds.getMaxX()).append(" ");
            sbInsets.append(Math.min(keyEventHandler.scene.getHeight(),out.getMaxY())-bounds.getMaxY()).append(" ");
            sbInsets.append(bounds.getMinX());
            if(cont.getAndIncrement()<keyEventHandler.group.getChildren().size()-1){
                sbColors.append(", ");
                sbInsets.append(", ");
            }
        });**/
        //keyEventHandler.textWindow.setStyle("-fx-background-color: "+sbColors.toString());
        //keyEventHandler.positioner.getCurrentNode().getData().setStyle("-fx-background-color: "+sbColors.toString()+"; -fx-background-insets: "+sbInsets.toString()+";");
    }



}
