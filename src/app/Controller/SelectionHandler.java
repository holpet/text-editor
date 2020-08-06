package app.Controller;
import app.Model.Clipboard;
import app.Model.MyText;
import app.Model.Node;
import app.Model.SelectableNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;

public class SelectionHandler {
    private Clipboard clipboard;
    public Positioner positioner;
    public HashMap<MyText, Node> hashMap;

    private EventHandler<MouseEvent> mousePressedEventHandler;

    public SelectionHandler(final Group group, KeyEventHandler keyEventHandler) {
        this.clipboard = new Clipboard();
        this.positioner = keyEventHandler.positioner;
        this.hashMap = keyEventHandler.hashMap;
        this.mousePressedEventHandler = event -> {
            SelectionHandler.this.doOnMousePressed(group, event);
            event.consume();
        };
    }

    public EventHandler<MouseEvent> getMousePressedEventHandler() {
        return mousePressedEventHandler;
    }

    private void doOnMousePressed(Group group, MouseEvent event) {
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

}
