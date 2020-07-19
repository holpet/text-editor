package app.Controller;

import app.Model.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

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

    public void deleteLetter(Text letter) {
        group.getChildren().remove(letter);
    }

    public void renderText() {
        Node tmp = linkedText.getHead();
        if (tmp == null) {
            //System.out.println("No text to be rendered.");
            return;
        }

        // Initial variables
        int posX = positioner.getPaddX();
        int posY = positioner.getPaddY();

        while (tmp != null) {
            // figure out letter size and position of a letter
            // leftText() // centerText() // rightText()
            double letterWidth = tmp.data.getLayoutBounds().getWidth();
            double letterHeight = tmp.data.getLayoutBounds().getHeight();
            int nextLetterWidth = 0;
            if (tmp.next != null) {
                nextLetterWidth = (int)tmp.next.data.getLayoutBounds().getWidth();
            }

            tmp.data.setX(posX);
            tmp.data.setY(posY);
            //tmp.data.toFront();

            // SET NEW CURSOR COORDS
            cursor.changeCursorPos( (posX+letterWidth), (posY-letterHeight) );

            if (!group.getChildren().contains(tmp.data)) {
                group.getChildren().add(tmp.data);
            }
            if (tmp.next != null) {
                tmp = tmp.next;
            }
            else {
                return;
            }
            NodeAndCoords nodeAndCoords = handleLines(tmp, nextLetterWidth, letterWidth, letterHeight, posX, posY);
            posX = nodeAndCoords.getCoordX();
            posY = nodeAndCoords.getCoordY();
            tmp = nodeAndCoords.getNode();
        }
    }

    public Boolean checkLineEnd(int newCoordX, int nextLetterWidth, int sceneWidth) {
        int leftSpace = 52;
        if ((newCoordX + nextLetterWidth) >= (sceneWidth - leftSpace)) {
            return true;
        }
        return false;
    }

    public NodeAndCoords handleLines(Node node, int nextLetterWidth, double letterWidth, double letterHeight, int initCoordX, int initCoordY) {
        int newCoordX = 0;
        int newCoordY = 0;
        NodeAndCoords nodeAndCoords = new NodeAndCoords(newCoordX, newCoordY, node);

        int sceneWidth = scene.widthProperty().intValue();

        // Calculate coord X for the next letter
        newCoordX = initCoordX + (int)letterWidth;

        // If next letter doesn't fit on the line:
        if (checkLineEnd(newCoordX, nextLetterWidth, sceneWidth)) {
            // If next letter is space:
            if (node.data.getText().equals(" ")) {
                // Add space on the line
                nodeAndCoords.setCoordX(newCoordX);
                nodeAndCoords.setCoordY(initCoordY);
                return nodeAndCoords;
            }
            // If next letter isn't space:
            else {
                // Move the word next letter is part of onto a new line
                while (node.prev != null) {
                    // Search backwards until you find space indicating the beginning of the word
                    if (node.prev.data.getText().equals(" ")) {
                        nodeAndCoords.setNode(node);
                        nodeAndCoords.setCoordX(0);
                        nodeAndCoords.setCoordY((initCoordY + (int)letterHeight));
                        return nodeAndCoords;
                    }
                    // Move onto the previous node
                    node = node.prev;
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
