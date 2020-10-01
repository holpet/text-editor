package app.Controller.Position;

import app.Model.Clipboard;
import app.Model.Cursor;
import app.Model.LinkedList;
import app.Model.Node;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Positioner {
    public LinkedList linkedText; // pointer to head node
    private Node currentNode; // pointer to current Node
    private BooleanProperty change; // detects changes in current node
    private Boolean cursorIsAtStart;
    public Cursor cursor;
    private final Clipboard clipboard;

    public Positioner(Cursor cursor, LinkedList linkedText) {
        this.linkedText = linkedText;
        this.currentNode = null;
        this.cursorIsAtStart = false;
        this.cursor = cursor;
        this.clipboard = new Clipboard();
        this.change = new SimpleBooleanProperty(false);
        //notifyCurrentNode();
    }

    public void updatePosition() {
        if (getCurrentNode() != null && !linkedText.isEmpty()) {
            double posX = getCurrentNode().getData().getX();
            double posY = getCurrentNode().getData().getY();
            double letterWidth = getCurrentNode().getData().getLayoutBounds().getWidth();

            if (!getCursorIsAtStart()) {
                cursor.changeCursorPos( (posX+letterWidth), (posY) );
            }
            else {
                if (getCurrentNode().getData().getX() == 0) {
                    posX = 0;
                }
                cursor.changeCursorPos( (posX), (posY) );
            }
            change.setValue(true);
        }
    }

    public void adjustUpdatedPosition() {
        if (getCurrentNode().getData().getX() != 0 && getCursorIsAtStart()) {
            setCursorIsAtStart(false);
            setCurrentNode(getCurrentNode().getPrev());
        }
    }

    public void notifyCurrentNode() {
        ChangeListener changeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                if (currentNode == null) {
                    clipboard.unselectAll();
                    change.setValue(false);
                }
                else {
                    clipboard.unselectAll();
                    clipboard.select(currentNode.getData(), true);
                    change.setValue(false);
                }
            }
        };
        change.addListener(changeListener);
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }


    public Boolean getCursorIsAtStart() {
        return cursorIsAtStart;
    }

    public void setCursorIsAtStart(Boolean data) {
        this.cursorIsAtStart = data;
    }

}
