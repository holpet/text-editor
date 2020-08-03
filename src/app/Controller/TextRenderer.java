package app.Controller;

import app.Model.*;
import javafx.scene.Group;
import javafx.scene.Scene;

public class TextRenderer {

    public Scene scene;
    public Group group;
    public LinkedList linkedText;
    public Positioner positioner;
    public Cursor cursor;

    public TextRenderer(Scene scene, Group group, LinkedList linkedText, Positioner positioner, Cursor cursor) {
        this.scene = scene;
        this.group = group;
        this.linkedText = linkedText;
        this.positioner = positioner;
        this.cursor = cursor;
    }

    public void renderText() {

        /** No text to render **/
        Node tmp = linkedText.getFirst();
        if (linkedText.isEmpty()) {
            return;
        }

        /** Start rendering text from the first letter **/
        // Initial variables
        int posX = 0;
        int posY = 0;

        while (!linkedText.isAtEnd(tmp)) {
            // figure out letter size and position of a letter
            // leftText() // centerText() // rightText()
            double letterWidth = tmp.getData().getLayoutBounds().getWidth();
            double letterHeight = tmp.getData().getLayoutBounds().getHeight();
            double nextLetterWidth = 0;
            if (!linkedText.isAtEnd(tmp.getNext())) {
                nextLetterWidth = tmp.getNext().getData().getLayoutBounds().getWidth();
            }

            tmp.getData().setX(posX);
            tmp.getData().setY(posY);
            //tmp.data.toFront();

            if (!group.getChildren().contains(tmp.getData())) {
                group.getChildren().add(tmp.getData());
                // Set cursor position when writing
                positioner.updatePosition();
            }
            if (!linkedText.isAtEnd(tmp.getNext())) {
                tmp = tmp.getNext();
            }
            else {
                return;
            }
            NodeAndCoords nodeAndCoords = handleLines(tmp, nextLetterWidth, letterWidth, letterHeight, posX, posY);
            posX = nodeAndCoords.getCoordX();
            posY = nodeAndCoords.getCoordY();
            tmp = nodeAndCoords.getNode();
        }
        // Set cursor position
        positioner.updatePosition();
    }

    public Boolean checkLineEnd(int newCoordX, double nextLetterWidth, int sceneWidth) {
        int leftSpace = 52;
        if ((newCoordX + nextLetterWidth) >= (sceneWidth - leftSpace)) {
            return true;
        }
        return false;
    }

    public NodeAndCoords handleLines(Node node, double nextLetterWidth, double letterWidth, double letterHeight, int initCoordX, int initCoordY) {
        int newCoordX = 0;
        int newCoordY = 0;
        NodeAndCoords nodeAndCoords = new NodeAndCoords(newCoordX, newCoordY, node);

        int sceneWidth = scene.widthProperty().intValue();

        // Calculate coord X for the next letter
        newCoordX = initCoordX + (int)letterWidth;

        // If next letter doesn't fit on the line:
        if (checkLineEnd(newCoordX, nextLetterWidth, sceneWidth)) {
            // If next letter is space:
            if (node.getData().getText().equals(" ")) {
                // Add space on the line
                nodeAndCoords.setCoordX(newCoordX);
                nodeAndCoords.setCoordY(initCoordY);
                return nodeAndCoords;
            }
            // If next letter isn't space:
            else {
                // Move the word next letter is part of onto a new line
                while (node.getPrev() != null) {
                    // Search backwards until you find space indicating the beginning of the word
                    if (node.getPrev().getData().getText().equals(" ")) {
                        nodeAndCoords.setNode(node);
                        nodeAndCoords.setCoordX(0);
                        nodeAndCoords.setCoordY((initCoordY + (int)letterHeight));
                        return nodeAndCoords;
                    }
                    // Move onto the previous node
                    node = node.getPrev();
                }
            }
        }
        // If next letter fits on the line:
        else {
            // Add next letter on the line
            nodeAndCoords.setCoordX(newCoordX);
            nodeAndCoords.setCoordY(initCoordY);
            return nodeAndCoords;
        }
        return nodeAndCoords;
    }

    private void moveTextCenter() {
        // Determine the width of space behind the last word on the line and before the first word on the line
        // Divide the sum by 2
        // Put half width left, half width right
    }

    private void moveTextLeft() {
        // Determine the width of space behind the last word on the line and before the first word on the line
        // Sum it up
        // Put all width to the right (each line starts with coordX 0)
    }

    public void moveTextRight() {
        // Determine the width of space behind the last word on the line and before the first word on the line
        // Sum it up
        // Put all width to the left (each line on the left starts with coordX = screen.widthProperty() - leftSpace // 52)
    }


}
