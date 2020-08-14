package app.Controller;

import app.Model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
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
    public HashMap<Integer, Node> hashMapIdx;
    private int lineCounter;

    public TextManipulator(Stage stage, Scene scene, Group group, LinkedList linkedText, Positioner positioner,
                           Cursor cursor, ScrollPane textWindow, HashMap<MyText, Node> hashMap, HashMap<Integer, Node> hashMapIdx) {
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
        this.hashMapIdx = hashMapIdx;
        this.lineCounter = 0;
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
        if (!pairSelection.isEmpty()) {
            deleteSelection();
        }
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
                //if (node.getPrev().getData().getText().equals(""))
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

        if (linkedText.isEmpty()) positioner.setCurrentNode(null);

        if (direction.equals("UP/LEFT")) {
            if (linkedText.isAtBeginning(end.getPrev())) {
                positioner.setCurrentNode(linkedText.getFirst());
                if (pairSelection.get(1).getValue()) {
                    positioner.setCursorIsAtStart(true);
                }
                else {
                    positioner.setCursorIsAtStart(false);
                }
            }
            else {
                positioner.setCurrentNode(end);
                positioner.setCursorIsAtStart(false);
            }
        }
        else { // "DOWN/RIGHT"
            if (linkedText.isAtBeginning(start.getPrev())) {
                positioner.setCurrentNode(linkedText.getFirst());
                if (pairSelection.get(0).getValue()) {
                    positioner.setCursorIsAtStart(true);
                }
                else {
                    positioner.setCursorIsAtStart(false);
                }
            }
            else {
                positioner.setCurrentNode(start);
                positioner.setCursorIsAtStart(false);
            }
        }
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
        if (keyEvent.isShiftDown() && !pairSelection.isEmpty()) {
            pairSelection.add(1, (new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart())));
        }

        switch (direction) {

            case "LEFT" -> {
                if (!linkedText.isAtBeginning(currentNode.getPrev())) {
                    // If letter is at the beginning of a line
                    if (currentNode.getData().getX() == 0) {
                        if (positioner.getCursorIsAtStart()) {
                            positioner.setCurrentNode(currentNode.getPrev());
                            positioner.setCursorIsAtStart(false);
                        }
                        else positioner.setCursorIsAtStart(true);
                    }
                    else {
                        positioner.setCurrentNode(currentNode.getPrev());
                    }
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
                    // If letter is at the beginning of a line
                    System.out.println("Right: " + currentNode.getData());
                    if (currentNode.getData().getX() == 0 && positioner.getCursorIsAtStart()) {
                        positioner.setCursorIsAtStart(false);
                        return;
                    }
                    else {
                        positioner.setCurrentNode(currentNode.getNext());
                    }
                    // If letter is at the end of a line
                    if (currentNode.getNext().getData().getY() > currentNode.getData().getY()) {
                        positioner.setCurrentNode(currentNode.getNext());
                        positioner.setCursorIsAtStart(true);
                    }
                    else {
                        positioner.setCurrentNode(currentNode.getNext());
                        positioner.setCursorIsAtStart(false);
                    }
                }
                positioner.updatePosition();
            }

            case "DOWN" -> {
                Node newCurrentNode = switchLines("DOWN");
                //System.out.println("Down: " + currentNode.getData());
                if (newCurrentNode != null) {
                    if (positioner.getCursorIsAtStart()) {
                        positioner.setCurrentNode(newCurrentNode);
                        positioner.setCursorIsAtStart(true);
                    }
                    else {
                        positioner.setCurrentNode(newCurrentNode);
                    }
                    positioner.updatePosition();
                }
            }

            case "UP" -> {
                Node newCurrentNode = switchLines("UP");
                if (newCurrentNode != null) {
                    if (positioner.getCursorIsAtStart()) {
                        positioner.setCurrentNode(newCurrentNode);
                        positioner.setCursorIsAtStart(true);
                    }
                    else {
                        positioner.setCurrentNode(newCurrentNode);
                    }
                    positioner.updatePosition();
                }
            }
        }
    }

    private Boolean checkBoundaries(MyText target, Double x) {
        Double targetX = target.getX();
        Double targetWidth = target.getLayoutBounds().getWidth();
        return (x >= targetX && x <= (targetX + targetWidth));
    }

    private Node findSwitchedNode(double centerX, double centerY, double letterHeight) {
        Node result;
        // Determine what line did the user click on using y coord
        Integer lineNum = Math.floorDiv((int)centerY, (int)letterHeight);
        Node lineNode = hashMapIdx.get(lineNum); // first node on a line
        if (lineNum > getLineCounter() || centerY < 0) {
            return null;
        }
        // Traverse through line to find the closest x coord
        Boolean found = false;
        while ((int)lineNode.getData().getY() == lineNum*(int)letterHeight) {
            found = checkBoundaries(lineNode.getData(), centerX);
            if (found) {
                result = lineNode;
                //System.out.println("Closest node found (T=0): " + result);
                return result;
            }
            lineNode = lineNode.getNext();
            if ( ((int)lineNode.getNext().getData().getY() != lineNum*(int)letterHeight) || linkedText.isAtEnd(lineNode.getNext())) {
                result = lineNode;
                //System.out.println("Closest node found (T=0): " + result);
                return result;
            }
        }
        return null;
    }

    private Node switchLines(String direction) {
        Node currentNode = positioner.getCurrentNode();
        double posX = currentNode.getData().getX();
        double posY = currentNode.getData().getY();
        double letterHeight = currentNode.getData().getLayoutBounds().getHeight();

        switch (direction) {

            case "DOWN" -> {
                double centerPointX = posX; // + letterWidth/2
                double centerPointY = posY + letterHeight + letterHeight/2;
                return findSwitchedNode(centerPointX, centerPointY, letterHeight);
            }

            case "UP" -> {
                double centerPointX = posX; //+ letterWidth/2
                double centerPointY = posY - letterHeight/2;
                return findSwitchedNode(centerPointX, centerPointY, letterHeight);
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

            Node start = hashMap.get(pairSelection.get(0).getKey().getData());
            Node end = hashMap.get(pairSelection.get(1).getKey().getData());
            double startY = start.getData().getBoundsInParent().getMinY();
            double endY = end.getData().getBoundsInParent().getMinY();

            switch (direction) {
                case "UP/LEFT" -> {
                    if (node.getData().getX() == 0 && cursorAtStart) {
                        node = node.getPrev();
                    }
                    while (node != pairSelection.get(1).getKey()) {
                        clipboard.select(node.getData(), true);
                        node = node.getPrev();
                    }
                    if (node.getData().getX() == 0 && positioner.getCursorIsAtStart()) {
                        clipboard.select(node.getData(), true);
                    }
                }
                case "DOWN/RIGHT" -> {
                    // right
                    if (node.getData().getX() == 0 && cursorAtStart && !positioner.getCursorIsAtStart()) {
                        clipboard.select(node.getData(), true);
                    }
                    // down
                    if (node.getData().getX() == 0 && cursorAtStart && positioner.getCursorIsAtStart() && (endY > startY)) {
                        clipboard.select(node.getData(), true);
                    }
                    while (node != pairSelection.get(1).getKey()) {
                        clipboard.select(node.getNext().getData(), true);
                        node = node.getNext();
                    }
                    if (positioner.getCursorIsAtStart()) clipboard.select(node.getData(), false);
                    System.out.println(positioner.getCurrentNode().getData());

                }
            }
        }
    }

    private String decideDirection() {
        Node start = hashMap.get(pairSelection.get(0).getKey().getData());
        Node end = hashMap.get(pairSelection.get(1).getKey().getData());
        Boolean cursorAtStart = pairSelection.get(0).getValue();
        Boolean cursorAtEnd = positioner.getCursorIsAtStart();
        double startX = start.getData().getBoundsInParent().getMinX();
        double startY = start.getData().getBoundsInParent().getMinY();
        double endX = end.getData().getBoundsInParent().getMinX();
        double endY = end.getData().getBoundsInParent().getMinY();
        if ( (endX < startX && endY == startY) || endY < startY ) return "UP/LEFT";
        if ( (endX > startX && endY == startY) || endY > startY ) return "DOWN/RIGHT";
        if (!cursorAtStart && cursorAtEnd) return "UP/LEFT";
        else return "DOWN/RIGHT";
    }


    /** ************************************************************************ **/
    /** ------------------------ SCROLL OPERATIONS ------------------------ **/

    public Double getVisibleAmount() {
        for (javafx.scene.Node node : textWindow.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    return scrollBar.getVisibleAmount();
                }
            }
        }
        return null;
    }

    public void handleScrollView(String direction) {
        Double visAmount = getVisibleAmount();
        if (visAmount >= 1) return;

        Double height = textWindow.getContent().getLayoutBounds().getHeight();
        Integer lineHeight = (int)cursor.getSampleLetter().getLayoutBounds().getHeight();
        Double lineDeltaY = ((height-height*visAmount) / getLineCounter())/100;

        System.out.println(visAmount);
        System.out.println("Line Delta: " + (height-height*visAmount) / getLineCounter()/100);

        //double h_size = (h_maxY - letterHeight*2) / h;
        if (direction.equals("UP")) textWindow.setVvalue(textWindow.getVvalue() - lineDeltaY);
        else textWindow.setVvalue(textWindow.getVvalue() + lineDeltaY);

    }

    public void showVisibleNodes() {
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

    public int getLineCounter() {
        return lineCounter;
    }

    public void setLineCounter(int lineCounter) {
        this.lineCounter = lineCounter;
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
