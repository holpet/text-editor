package app.Controller;

import app.Model.Clipboard;
import app.Model.Cursor;
import app.Model.LinkedList;
import app.Model.Node;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Positioner {
    public LinkedList linkedText; // pointer to head node
    private Node currentNode; // pointer to current Node
    private ObjectProperty<Node> currentNodeNotice;
    // cursor appears AFTER currently selected letter, BEFORE only if user clicked on the left side of the letter and /current node == head node/
    private Boolean cursorIsAtStart;
    public Cursor cursor;
    private Clipboard clipboard;

    public Positioner(Cursor cursor, LinkedList linkedText) {
        this.linkedText = linkedText;
        this.currentNode = null;
        initiateNotice();
        this.cursorIsAtStart = false;
        this.cursor = cursor;
        this.clipboard = new Clipboard();
    }

    public void updatePosition() {
        if (getCurrentNode() != null && !linkedText.isEmpty()) {
            double posX = getCurrentNode().getData().getX();
            double posY = getCurrentNode().getData().getY();
            double letterWidth = getCurrentNode().getData().getLayoutBounds().getWidth();
            double letterHeight = getCurrentNode().getData().getLayoutBounds().getHeight();
            if (!getCursorIsAtStart()) {
                cursor.changeCursorPos( (posX+letterWidth), (posY-letterHeight+2) );
            }
            else {
                cursor.changeCursorPos( (posX), (posY-letterHeight+2) );
            }
        }
    }

    public void notifyCurrentNode() {
        ChangeListener<Node> changeListener = new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> observableValue, Node oldCurrentNode, Node newCurrentNode) {
                if (currentNode == null) {
                    clipboard.unselectAll();
                }
                else {
                    clipboard.unselectAll();
                    clipboard.select(currentNode.getData(), true);
                }
            }
        };
        currentNodeNotice.addListener(changeListener);
    }


    private void initiateNotice() {
        currentNodeNotice = new ObjectProperty<Node>() {
            @Override
            public void bind(ObservableValue<? extends Node> observableValue) {

            }

            @Override
            public void unbind() {

            }

            @Override
            public boolean isBound() {
                return false;
            }

            @Override
            public Object getBean() {
                return null;
            }

            @Override
            public String getName() {
                return currentNode.getData().toString();
            }

            @Override
            public Node get() {
                return currentNode;
            }

            @Override
            public void addListener(ChangeListener<? super Node> changeListener) {

            }

            @Override
            public void removeListener(ChangeListener<? super Node> changeListener) {

            }

            @Override
            public void addListener(InvalidationListener invalidationListener) {

            }

            @Override
            public void removeListener(InvalidationListener invalidationListener) {

            }

            @Override
            public void set(Node node) {

            }
        };
        Bindings.createObjectBinding(() -> {
            currentNodeNotice.set(currentNode);
            return null;
        }, currentNodeNotice);
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
