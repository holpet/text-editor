package app.Controller;

import app.Model.Cursor;
import app.Model.LinkedList;
import app.Model.Node;

public class Positioner {
    public LinkedList linkedText; // pointer to head node
    private Node currentNode; // pointer to current Node
    // cursor appears AFTER currently selected letter, BEFORE only if user clicked on the left side of the letter and /current node == head node/
    private Boolean cursorIsAtStart;
    public Cursor cursor;

    public Positioner(Cursor cursor, LinkedList linkedText) {
        this.linkedText = linkedText;
        this.currentNode = null;
        this.cursorIsAtStart = false;
        this.cursor = cursor;
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
