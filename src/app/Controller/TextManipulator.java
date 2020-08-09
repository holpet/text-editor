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
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class TextManipulator {
    public Stage stage;
    public Scene scene;
    public Group group;
    public LinkedList linkedText;
    public Positioner positioner;
    public Cursor cursor;
    public ScrollPane textWindow;
    public final ArrayList<Pair<Node, Boolean>> pairSelection;
    public final Clipboard clipboard;
    public HashMap<MyText, Node> hashMap;

    public TextManipulator(Stage stage, Scene scene, Group group, LinkedList linkedText, Positioner positioner,
                           Cursor cursor, ScrollPane textWindow, HashMap<MyText, Node> hashMap) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.linkedText = linkedText;
        this.positioner = positioner;
        this.cursor = cursor;
        this.textWindow = textWindow;
        this.pairSelection = new ArrayList<>();
        this.clipboard = new Clipboard();
        this.hashMap = hashMap;
    }

    /** ************************************************************************ **/
    /** ------------------------ ADD/DELETE TEXT TO GROUP ------------------------ **/
    public void addToGroup(Node node) {
        group.getChildren().add(node.getData());
        group.getChildren().add(node.getData().getBackground());
        node.getData().getBackground().toBack();
    }

    public void deleteFromGroup(Node node) {
        group.getChildren().remove(node.getData());
        group.getChildren().remove(node.getData().getBackground());
    }

    /** ************************************************************************ **/
    /** ------------------------ CREATE/DELETE TEXT ------------------------ **/
    public void createLetter(String letter, String fontName, int fontSize) {
        MyText text = createLetter_simplified(letter, fontName, fontSize);
        // Insert node into linkedList, update position and save it in a hashMap
        linkedText.insertAt(text, positioner);
    }

    // Without changing current node and cursor/caret
    public MyText createLetter_simplified (String letter, String fontName, int fontSize) {
        MyText text = new MyText();
        text.setText(letter);
        text.setFont(Font.font(fontName, fontSize));
        return text;
    }

    public void deleteLetter(String key) {
        Node currentNode = positioner.getCurrentNode();
        if (!clipboard.getSelectedItems().isEmpty() || currentNode == null) return;

        switch (key) {
            case "BACK_SPACE" -> {
                Node node = linkedText.deleteAt_BACKSPACE(currentNode, positioner);
                if (node != null) {
                    deleteFromGroup(node);
                }
                positioner.updatePosition();
            }
            case "DELETE" -> {
                Node node = linkedText.deleteAt_DELETE(currentNode, positioner);
                if (node != null) {
                    deleteFromGroup(node);
                }
                positioner.updatePosition();
            }
        }
    }

    public void deleteSelection() {
        if (clipboard.getSelectedItems().isEmpty()) return;

        Node start = hashMap.get(pairSelection.get(0).getKey().getData());
        Node end = hashMap.get(pairSelection.get(1).getKey().getData());
        String direction = decideDirection();

        for (SelectableNode text : clipboard.getSelectedItems()) {
            Node node = hashMap.get(text);
            linkedText.deleteAt_simplified(node);
            deleteFromGroup(node);
        }
        System.out.println("After DEL: " + positioner.getCurrentNode().getData());

        if (linkedText.isEmpty()) positioner.setCurrentNode(null);
        if (direction.equals("UP/LEFT")) {
            if (linkedText.isAtBeginning(end.getPrev())) {
                positioner.setCurrentNode(linkedText.getFirst());
                positioner.setCursorIsAtStart(true);
            }
            else {
                positioner.setCurrentNode(end);
                positioner.setCursorIsAtStart(false);
            }
        }
        else { // "DOWN/RIGHT"
            if (linkedText.isAtBeginning(start.getPrev())) {
                positioner.setCurrentNode(linkedText.getFirst());
                positioner.setCursorIsAtStart(true);
            }
            else {
                positioner.setCurrentNode(start);
                positioner.setCursorIsAtStart(false);
            }
        }
        System.out.println("Cur sel NODE: " + positioner.getCurrentNode().getData());
        positioner.updatePosition();
        clipboard.unselectAll();
        pairSelection.clear();
    }


    /** ************************************************************************ **/
    /** ------------------------ MOVE CURSOR BY LETTERS ------------------------ **/
    public void moveByLetter(String direction, KeyEvent keyEvent) {

        Node currentNode = positioner.getCurrentNode();
        if (currentNode == null) return;

        // Initial position before move
        if (keyEvent.isShiftDown()) {
            pairSelection.add(1, (new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart())));
        }

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
                    if (!positioner.getCursorIsAtStart()) {
                        positioner.setCurrentNode(currentNode.getNext());
                    }
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


    /** ************************************************************************ **/
    /** ------------------------ SELECTION OPERATIONS ------------------------ **/
    public void moveSelection() {
        // If a node isn't added to selection, return
        if (pairSelection.isEmpty()) {
            return;
        }
        // If a starting/ending node is added to selection
        //Node node = selection.get(0);
        Node node = pairSelection.get(0).getKey();
        Boolean cursorAtStart = pairSelection.get(0).getValue();

        if (pairSelection.size() == 1) {
            // Select-notify node in selection
            clipboard.unselectAll();
            clipboard.select(node.getData(), true);
        }
        else { // If pairSelection.size() == 2
            // Go through linkedList and select-notify all nodes in selection, incl. first and last node
            String direction = decideDirection();
            clipboard.unselectAll();

            switch (direction) {
                case "UP/LEFT" -> {
                    while (node != pairSelection.get(1).getKey()) {
                        clipboard.select(node.getData(), true);
                        node = node.getPrev();
                    }
                    if (linkedText.isAtBeginning(node.getPrev()) && positioner.getCursorIsAtStart()) {
                        System.out.println("UP/LEFT: is at beginning...");
                        System.out.println(node.getData());
                        if (!cursorAtStart) clipboard.select(node.getData(), true);
                    }
                }
                case "DOWN/RIGHT" -> {
                    if (linkedText.isAtBeginning(node.getPrev()) && !positioner.getCursorIsAtStart()) {
                        System.out.println("DOWN/RIGHT: is at beginning...");
                        System.out.println(node.getData());
                        if (cursorAtStart) clipboard.select(node.getData(), true);
                    }
                    while (node != pairSelection.get(1).getKey()) {
                        clipboard.select(node.getNext().getData(), true);
                        node = node.getNext();
                    }

                }
            }
        }
    }

    private String decideDirection() {
        Node start = hashMap.get(pairSelection.get(0).getKey().getData());
        Node end = hashMap.get(pairSelection.get(1).getKey().getData());
        double startX = start.getData().getBoundsInParent().getMinX();
        double startY = start.getData().getBoundsInParent().getMinY();
        double endX = end.getData().getBoundsInParent().getMinX();
        double endY = end.getData().getBoundsInParent().getMinY();
        if ( (endX < startX && endY == startY) || endY < startY ) return "UP/LEFT";
        if ( (endX > startX && endY == startY) || endY > startY ) return "DOWN/RIGHT";
        if (positioner.getCursorIsAtStart()) return "UP/LEFT";
        else return "DOWN/RIGHT";
    }


    /** ************************************************************************ **/
    /** ------------------------ SCROLL OPERATIONS ------------------------ **/
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

    /** ************************************************************************ **/
    /** ------------------------ CENTER/LEFT/RIGHT FULL TEXT ------------------------ **/
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
