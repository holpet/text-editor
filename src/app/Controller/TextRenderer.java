package app.Controller;

import app.Model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TextRenderer {

    public Stage stage;
    public Scene scene;
    public Group group;
    public LinkedList linkedText;
    public Positioner positioner;
    public Cursor cursor;
    public ScrollPane textWindow;
    private int lineCounter;
    private ArrayList<Node> dividers;

    public TextRenderer(Stage stage, Scene scene, Group group, LinkedList linkedText, Positioner positioner, Cursor cursor, ScrollPane textWindow) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.linkedText = linkedText;
        this.positioner = positioner;
        this.cursor = cursor;
        this.textWindow = textWindow;
        this.lineCounter = 1;
        this.dividers = new ArrayList<>();
    }

    public MyText createLetter(String letter, String fontName, int fontSize) {
        MyText text = new MyText();
        text.setText(letter);
        text.setFont(Font.font(fontName, fontSize));
        // Insert node into linkedList, update position and save it in a hashMap
        linkedText.insertAt(text, positioner);
        return text;
    }

    public MyText createLetter_simplified(String letter, String fontName, int fontSize) {
        MyText text = new MyText();
        text.setText(letter);
        text.setFont(Font.font(fontName, fontSize));
        return text;
    }

    public void deleteLetter(String key) {
        Node currentNode = positioner.getCurrentNode();
        if (currentNode == null) return;

        switch (key) {
            case "BACK_SPACE" -> {
                Node node = linkedText.deleteAt_BACKSPACE(currentNode, positioner);
                if (node != null) {
                    System.out.println("Is deleted (BS)?: " + group.getChildren().remove(node.getData()));
                }
                positioner.updatePosition();
            }
            case "DELETE" -> {
                Node node = linkedText.deleteAt_DELETE(currentNode, positioner);
                if (node != null) {
                    System.out.println("Is deleted? (DL): " + group.getChildren().remove(node.getData()));
                }
                positioner.updatePosition();
            }
        }
    }

    public void renderText() {

        /** No text to render **/
        Node tmp = linkedText.getFirst();
        if (linkedText.isEmpty()) {
            return;
        }
        lineCounter = 1;

        /** Start rendering text from the first letter **/
        // Initial variables
        int posX = 0;
        int posY = 0;
        // Delete all dividers from linkedList and clear it for start

        for (Node node : dividers) {
            if (positioner.getCurrentNode() == node) {
                positioner.setCurrentNode(node.getPrev());
            }
            linkedText.deleteAt_simplified(node);
            group.getChildren().remove(node.getData());
        }
        dividers.clear();

        //linkedText.printAll();

        while (!linkedText.isAtEnd(tmp)) {
            // figure out letter size and position of a letter
            double letterWidth = tmp.getData().getLayoutBounds().getWidth();
            double letterHeight = tmp.getData().getLayoutBounds().getHeight();
            double nextLetterWidth = 0;
            if (!linkedText.isAtEnd(tmp.getNext())) {
                nextLetterWidth = tmp.getNext().getData().getLayoutBounds().getWidth();
            }

            tmp.getData().setX(posX);
            tmp.getData().setY(posY);
            tmp.toFront();

            if (!group.getChildren().contains(tmp.getData())) {
                group.getChildren().add(tmp.getData());
                // Set cursor position when writing
                positioner.updatePosition();
            }
            if (!linkedText.isAtEnd(tmp.getNext())) {
                tmp = tmp.getNext();
            }
            else {
                //positioner.linkedText.printAll();
                handleScrollView();
                return;
            }
            NodeAndCoords nodeAndCoords = handleLines(tmp, nextLetterWidth, letterWidth, letterHeight, posX, posY);
            posX = nodeAndCoords.getCoordX();
            posY = nodeAndCoords.getCoordY();
            tmp = nodeAndCoords.getNode();
        }
    }

    public Boolean checkLineEnd(int newCoordX, double nextLetterWidth) {
        //System.out.println("Scroll Bounds Width: " + textWindow.getViewportBounds().getWidth());
        //System.out.println("Scene width: " + scene.getWidth());
        //System.out.println("Sc Bar width: " + textWindow.getVmax() + " " + textWindow.getVmin());
        //int leftSpace = 52;
        double leftSpace = scene.getWidth() - textWindow.getViewportBounds().getWidth();
        if ((newCoordX + nextLetterWidth) >= (scene.getWidth() - leftSpace)) {
            return true;
        }
        return false;
    }

    public NodeAndCoords handleLines(Node node, double nextLetterWidth, double letterWidth, double letterHeight, int initCoordX, int initCoordY) {
        int newCoordX = 0;
        int newCoordY = 0;
        NodeAndCoords nodeAndCoords = new NodeAndCoords(newCoordX, newCoordY, node);

        // Calculate coord X for the next letter
        newCoordX = initCoordX + (int)letterWidth;

        // If next letter doesn't fit on the line:
        if (checkLineEnd(newCoordX, nextLetterWidth)) {
            // If next letter is space:
            if (node.getData().getText().equals(" ")) {
                // Add space on the line
                nodeAndCoords.setCoordX(newCoordX);
                nodeAndCoords.setCoordY(initCoordY);
                return nodeAndCoords;
            }
            // If next letter isn't space:
            else {
                Node curNode = node;
                // Move the word next letter is part of onto a new line
                while (node.getPrev() != null) {
                    // If no space or "-" found on the line, stop searching
                    if (node.getData().getX() == 0) {
                        nodeAndCoords.setNode(curNode);
                        nodeAndCoords.setCoordX(0);
                        nodeAndCoords.setCoordY((initCoordY + (int)letterHeight));
                        // Add line to the counter
                        lineCounter++;
                        return nodeAndCoords;
                    }
                    // Otherwise, search backwards until you find space/- indicating the beginning of the word
                    if (node.getPrev().getData().getText().equals(" ") || node.getPrev().getData().getText().equals("-")) {
                        nodeAndCoords.setNode(node);
                        nodeAndCoords.setCoordX(0);
                        nodeAndCoords.setCoordY((initCoordY + (int)letterHeight));
                        // Add line to the counter
                        lineCounter++;
                        return nodeAndCoords;
                    }
                    // Move onto the previous node
                    node = node.getPrev();
                }
                /**
                // One line contains letters with no space or divider -> divide letter with "-"
                MyText letter = createLetter_simplified("-", positioner.getCurrentNode().getData().getFont().getName(),
                        (int)positioner.getCurrentNode().getData().getFont().getSize());
                Node div = linkedText.insertAt_simplified(letter, curNode.getPrev().getPrev());
                div.getData().setX(initCoordX);
                div.getData().setY(initCoordY);
                group.getChildren().add(div.getData());
                //positioner.updatePosition();

                // Add node in ArrayList "dividers"
                dividers.add(div);
                //linkedText.printAll();
                System.out.println(positioner.getCurrentNode().getData());

                nodeAndCoords.setNode(div.getNext());
                nodeAndCoords.setCoordX(0);
                nodeAndCoords.setCoordY(initCoordY + (int)letterHeight);
                return nodeAndCoords;
                 **/
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

    public void moveByLetter(String direction) {
        Node currentNode = positioner.getCurrentNode();
        if (currentNode == null) return;

        switch (direction) {

            case "LEFT" -> {
                if (!linkedText.isAtBeginning(currentNode.getPrev())) {
                    positioner.setCurrentNode(currentNode.getPrev());
                    positioner.setCursorIsAtStart(false);
                }
                else {
                    if (!positioner.getCursorIsAtStart()) {
                        positioner.setCursorIsAtStart(true);
                    }
                }
                positioner.updatePosition();
            }

            case "RIGHT" -> {
                if (!linkedText.isAtEnd(currentNode.getNext())) {
                    positioner.setCurrentNode(currentNode.getNext());
                }
                positioner.setCursorIsAtStart(false);
                positioner.updatePosition();
            }

            case "DOWN" -> {
                Node newCurrentNode = switchLines("DOWN");
                if (newCurrentNode != null) {
                    positioner.setCurrentNode(newCurrentNode);
                    positioner.setCursorIsAtStart(false);
                    positioner.updatePosition();
                }
            }

            case "UP" -> {
                Node newCurrentNode = switchLines("UP");
                if (newCurrentNode != null) {
                    positioner.setCurrentNode(newCurrentNode);
                    positioner.setCursorIsAtStart(false);
                    positioner.updatePosition();
                }
            }
        }
    }

    private Node findSwitchedNode(Node node, double centerX, double centerY, double letterHeight, String direction) {
        double X = node.getData().getX();
        double Y = node.getData().getY();
        double letterWidth = node.getData().getLayoutBounds().getWidth();

        // If linkedList is at the end -> no node found, return null
        if (linkedText.isAtEnd(node)) {
            return null;
        }
        // Search for a node in corresponding vertical and horizontal position and return it if found
        if ((centerY >= Y && centerY <= (Y+letterHeight)) && (centerX >= X && centerX <= (X+letterWidth))) {
            System.out.println("Switched Node: " + node.getData());
            return node;
        }
        // If you can't find the node
        else {
            // Continue searching in the linkedList
            switch (direction) {
                case "DOWN" -> {
                    return findSwitchedNode(node.getNext(), centerX, centerY, letterHeight, "DOWN");
                }
                case "UP" -> {
                    return findSwitchedNode(node.getPrev(), centerX, centerY, letterHeight, "UP");
                }
                default -> throw new IllegalStateException("Unexpected value: " + direction);
            }
        }
    }

    private Node switchLines(String direction) {
        Node currentNode = positioner.getCurrentNode();
        double posX = currentNode.getData().getX();
        double posY = currentNode.getData().getY();
        double letterWidth = currentNode.getData().getLayoutBounds().getWidth();
        double letterHeight = currentNode.getData().getLayoutBounds().getHeight();
        //handleScrollView();

        switch (direction) {

            case "DOWN" -> {
                Node tmp = currentNode.getNext();
                double centerPointX = posX + letterWidth/2;
                double centerPointY = posY + letterHeight + letterHeight/2;
                return findSwitchedNode(tmp, centerPointX, centerPointY, letterHeight, "DOWN");
            }

            case "UP" -> {
                Node tmp = currentNode.getPrev();
                double centerPointX = posX + letterWidth/2;
                double centerPointY = posY - letterHeight/2;
                return findSwitchedNode(tmp, centerPointX, centerPointY, letterHeight, "UP");
            }
        }
        return null;
    }

    public void handleScrollView() {
        // ADD TO renderText() ... had to check for where currentNode is at any moment
        //double c = textWindow.getContent().getBoundsInLocal().getHeight(); // SCROLLPANE CONTENT
        double v = textWindow.getViewportBounds().getHeight(); // VIEWPORT
        double vMinY = textWindow.getViewportBounds().getMinY();
        double vMaxY = textWindow.getViewportBounds().getMaxY();
        //System.out.println("Viewport height: " + v + ", V Min: " + vMinY + ", V Max: " + vMaxY);

        Node currentNode = positioner.getCurrentNode();
        if (currentNode == null) return;

        ObjectBinding<Bounds> visibleBounds = Bindings.createObjectBinding(() -> {
            Bounds viewportBounds = textWindow.getViewportBounds();
            Bounds viewportBoundsInScene = textWindow.localToScene(viewportBounds);
            return group.sceneToLocal(viewportBoundsInScene);
        }, textWindow.hvalueProperty(), textWindow.vvalueProperty(), textWindow.viewportBoundsProperty());

        // keep current node in viewport
        double letterHeight = positioner.getCurrentNode().getData().getLayoutBounds().getHeight();
        double h = textWindow.getBoundsInLocal().getHeight(); // SCROLLPANE
        double h_maxY = positioner.getCurrentNode().getData().getBoundsInParent().getMaxY();
        double h_size;


        // Down direction or up direction
        //if (currentNode.getBoundsInParent().getMaxY() )

        h_size = (h_maxY - letterHeight*2) / h;
        textWindow.setVvalue(h_size);

    }

    private void showVisibleNodes() {
        ObjectBinding<Bounds> visibleBounds = Bindings.createObjectBinding(() -> {
            Bounds viewportBounds = textWindow.getViewportBounds();
            Bounds viewportBoundsInScene = textWindow.localToScene(viewportBounds);
            return group.sceneToLocal(viewportBoundsInScene);
        }, textWindow.hvalueProperty(), textWindow.vvalueProperty(), textWindow.viewportBoundsProperty());


        FilteredList<javafx.scene.Node> visibleNodes = new FilteredList<>(group.getChildren());
        visibleNodes.predicateProperty().bind(Bindings.createObjectBinding(() ->
                node -> node.getBoundsInParent().intersects(visibleBounds.get()), visibleBounds));


        visibleNodes.addListener((ListChangeListener.Change<? extends javafx.scene.Node> c) -> {
            visibleNodes.forEach(System.out::println);
            System.out.println();
        });
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


    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
    }
}
