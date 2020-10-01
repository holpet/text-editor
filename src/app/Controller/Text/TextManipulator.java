package app.Controller.Text;

import app.Controller.Position.Positioner;
import app.Model.*;
import app.Model.Command.CommandList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
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
    public ArrayList<Pair<Node, Boolean>> pairSelection;

    public Clipboard clipboard;
    public final javafx.scene.input.Clipboard systemClipboard;
    public final ClipboardContent systemClipboardContent;

    public ObservableList<SelectableNode> copiedSelection;
    public ArrayList<Pair<Node, Boolean>> copiedPairList;

    public HashMap<MyText, Node> hashMap;
    public HashMap<Integer, Node> hashMapIdx;
    public CommandList commandList;
    private int lineCounter;
    public ScrollBar scrollBar;

    public Boolean toInsertSelection;

    public TextManipulator(Stage stage, Scene scene, Group group, LinkedList linkedText, Positioner positioner,
                           Cursor cursor, ScrollPane textWindow, HashMap<MyText, Node> hashMap, HashMap<Integer, Node> hashMapIdx,
                           CommandList commandList) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.linkedText = linkedText;
        this.positioner = positioner;
        this.cursor = cursor;
        this.textWindow = textWindow;
        this.pairSelection = new ArrayList<>();
        this.clipboard = new Clipboard();
        this.systemClipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        this.systemClipboardContent = new ClipboardContent();
        this.copiedSelection = null;
        this.copiedPairList = null;
        this.hashMap = hashMap;
        this.hashMapIdx = hashMapIdx;
        this.commandList = commandList;
        this.lineCounter = 0;
        this.toInsertSelection = false;
        this.scrollBar = null;
    }

    /** ************************************************************************ **/
    /** ------------------------ ADD/DELETE TEXT TO/FROM GROUP ------------------------ **/
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
    /** ---------------------------- CREATE LETTER ----------------------------- **/

    // Without changing current node and cursor/caret
    public MyText createLetter_simplified (String letter, String fontName, int fontSize) {
        MyText text = new MyText();
        text.setText(letter);
        text.setFont(Font.font(fontName, fontSize));
        return text;
    }

    public MyText createLetter(String letter, String fontName, int fontSize) {
        MyText text = createLetter_simplified(letter, fontName, fontSize);
        // Insert node into linkedList, update position and save it in a hashMap
        if (!pairSelection.isEmpty() && !toInsertSelection) {
            deleteSelection();
        }
        linkedText.insertAt(text, positioner);
        return text;
    }


    /** --------------------------- DELETE LETTER ----------------------------- **/

    public Node deleteLetter(String key) {
        Node currentNode = positioner.getCurrentNode();
        if (!clipboard.getSelectedItems().isEmpty() || currentNode == null) return null;

        switch (key) {
            case "BACK_SPACE" -> {
                Node node = linkedText.deleteAt_BACKSPACE(currentNode, positioner);
                if (node != null) {
                    deleteFromGroup(node);
                }
                return node;
            }
            case "DELETE" -> {
                Node node = linkedText.deleteAt_DELETE(currentNode, positioner);
                if (node != null) {
                    deleteFromGroup(node);
                }
                return node;
            }
        }
        return null;
    }

    /** ---------------------------- DELETE SELECTION ----------------------------- **/

    public ArrayList<Node> deleteSelection() {
        if (clipboard.getSelectedItems().isEmpty() || clipboard.getSelectedItems().size() < 2) return null;

        Node start = hashMap.get(pairSelection.get(0).getKey().getData());
        Node end = hashMap.get(pairSelection.get(1).getKey().getData());
        String direction = decideDirection();

        ArrayList<Node> deletedNodes = new ArrayList<>();

        for (SelectableNode text : clipboard.getSelectedItems()) {
            Node node = hashMap.get(text);
            if (node == null) break;
            Node deletedNode = linkedText.deleteAt_simplified(node);
            deletedNodes.add(deletedNode);
            deleteFromGroup(node);
        }

        if (linkedText.isEmpty()) {
            positioner.setCurrentNode(null);
        }

        if (direction.equals("UP/LEFT")) {
            if (end.getData().getX() == 0 && pairSelection.get(1).getValue()) {
                if (linkedText.isAtBeginning(end.getPrev())) {
                    positioner.setCurrentNode(start.getNext());
                    positioner.setCursorIsAtStart(true);
                }
                else {
                    positioner.setCurrentNode(end.getPrev());
                    positioner.setCursorIsAtStart(false);
                }
            }
            else {
                positioner.setCurrentNode(end);
                positioner.setCursorIsAtStart(false);
            }
        }
        else { // "DOWN/RIGHT"
            if (start.getData().getX() == 0 && pairSelection.get(0).getValue()) {
                if (linkedText.isAtBeginning(start.getPrev())) {
                    positioner.setCurrentNode(end.getNext());
                    positioner.setCursorIsAtStart(true);
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
        return deletedNodes;
    }


    /** ************************************************************************ **/
    /** ------------------------ MOVE CURSOR BY LETTERS ------------------------ **/
    public void moveByLetter(String direction, KeyEvent keyEvent) {

        Node currentNode = positioner.getCurrentNode();
        if (currentNode == null) return;
        if (!clipboard.getSelectedItems().isEmpty() && !keyEvent.isShiftDown()) {
            clipboard.unselectAll();
            //Treturn;
        }

        // Initial position before move
        if (keyEvent.isShiftDown() && !pairSelection.isEmpty()) {
            pairSelection.add(1, (new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart())));
        }

        switch (direction) {

            case "LEFT" -> {
                if (linkedText.isAtBeginning(currentNode.getPrev()) && positioner.getCursorIsAtStart()) return;
                // If letter is at the beginning of a line
                if (currentNode.getData().getX() == 0) {
                    if (!positioner.getCursorIsAtStart()) {
                        if (currentNode.getData().getText().equals("")) {
                            positioner.setCurrentNode(currentNode.getPrev());
                            positioner.setCursorIsAtStart(false);
                        }
                        else {
                            positioner.setCursorIsAtStart(true);
                        }
                    }
                    // Cursor at start
                    else {
                        if (currentNode.getPrev().getData().getText().equals("")) {
                            positioner.setCurrentNode(currentNode.getPrev().getPrev());
                            positioner.setCursorIsAtStart(false);
                        }
                        else {
                            positioner.setCurrentNode(currentNode.getPrev());
                            positioner.setCursorIsAtStart(false);
                        }
                    }
                }
                // If it's not at beginning of a line
                else {
                    positioner.setCurrentNode(currentNode.getPrev());
                    positioner.setCursorIsAtStart(false);
                }
                positioner.updatePosition();
            }

            case "RIGHT" -> {
                if (linkedText.isAtEnd(currentNode.getNext())) return;
                // If letter is at the beginning of a line
                if (currentNode.getData().getX() == 0) {
                    if (positioner.getCursorIsAtStart()) {
                        if (currentNode.getData().getText().equals("")) {
                            positioner.setCurrentNode(currentNode.getNext());
                            if (currentNode.getNext().getData().getY() > currentNode.getData().getY()) {
                                positioner.setCursorIsAtStart(true);
                            }
                            else {
                                positioner.setCursorIsAtStart(false);
                            }
                        }
                        else {
                            positioner.setCursorIsAtStart(false);
                        }
                    }
                    else {
                        positioner.setCurrentNode(currentNode.getNext());
                        positioner.setCursorIsAtStart(false);
                    }
                }
                // If letter is at the end of a line
                else if (currentNode.getNext().getData().getY() > currentNode.getData().getY()) {
                    positioner.setCurrentNode(currentNode.getNext());
                    positioner.setCursorIsAtStart(true);
                }
                else {
                    positioner.setCurrentNode(currentNode.getNext());
                    positioner.setCursorIsAtStart(false);
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
        // Determine what line did the user click on using y coord
        Integer lineNum = Math.floorDiv((int)centerY, (int)letterHeight);
        Node lineNode = hashMapIdx.get(lineNum); // first node on a line
        if (lineNum > getLineCounter() || centerY < 0) {
            return null;
        }
        // Traverse through line to find the closest x coord
        while ((int)lineNode.getData().getY() == lineNum*(int)letterHeight) {
            Boolean found = checkBoundaries(lineNode.getData(), centerX);
            if (found) {
                //System.out.println("Closest node found (T=0): " + result);
                return lineNode;
            }
            lineNode = lineNode.getNext();
            if ( ((int)lineNode.getData().getY() != lineNum*(int)letterHeight) || linkedText.isAtEnd(lineNode)) {
                //System.out.println("Closest node found (T=0): " + result);
                return lineNode.getPrev();
            }
        }
        return null;
    }

    private Node switchLines(String direction) {
        Node currentNode = positioner.getCurrentNode();
        double posX = currentNode.getData().getX();
        double posY = currentNode.getData().getY();
        double letterHeight = currentNode.getData().getLayoutBounds().getHeight();
        double letterWidth = currentNode.getData().getLayoutBounds().getWidth();

        switch (direction) {

            case "DOWN" -> {
                double centerPointX = posX + letterWidth/2;
                double centerPointY = posY + letterHeight + letterHeight/2;
                return findSwitchedNode(centerPointX, centerPointY, letterHeight);
            }

            case "UP" -> {
                double centerPointX = posX + letterWidth/2;
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
                    //System.out.println(positioner.getCurrentNode().getData());

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
    /** ------------------------ SYSTEM CLIPBOARD ------------------------ **/

    public void copyText() {
        // Copy text to system clipboard
        String text = "";
        for (SelectableNode myText : clipboard.getSelectedItems()) {
            Node selectedNode = hashMap.get(myText);
            String selectedText = selectedNode.getData().getText();
            if (selectedText.equals("")) {
                selectedText = "" + '\n';
            }
            text = text + selectedText;
        }
        if (decideDirection().equals("UP/LEFT")) {
            text = new StringBuilder(text).reverse().toString();
        }
        systemClipboardContent.putString(text);
        systemClipboard.setContent(systemClipboardContent);
    }

    public String getTextFromClipboard() {
        String text = null;
        try {
            text = systemClipboardContent.getString();
        }
        catch (Exception e) {
            System.out.println("Error getting system clipboard: " + e);
        }

        if (text == null) {
            /** Clipboard system selection **/
            //text = (String) Toolkit.getToolkit().getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
            System.out.println("String from Clipboard2 --- system:" + text);
        }

        if (text == null || text.isEmpty()) return null;
        else return text;
    }


    /** ************************************************************************ **/
    /** ------------------------ SCROLL OPERATIONS ------------------------ **/

    public void setScrollBar() {
        for (javafx.scene.Node node : textWindow.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    this.scrollBar = scrollBar;
                }
            }
        }
    }

    public Double getVisibleAmount() {
        return scrollBar.getVisibleAmount();
    }

    public void handleScrollView() {
        double heightViewPort = textWindow.getViewportBounds().getHeight();
        double heightScrollPane = textWindow.getContent().getBoundsInLocal().getHeight();
        double y = positioner.getCurrentNode().getData().getBoundsInParent().getMaxY();
        if (y<(heightViewPort/2)) {
            // below 0 of scrollpane
            textWindow.setVvalue(0);
        }
        else if ((y>=(heightViewPort/2))&(y<=(heightScrollPane-heightViewPort/2))) {
            // between 0 and 1 of scrollpane
            textWindow.setVvalue((y-(heightViewPort/2))/(heightScrollPane-heightViewPort));
        }
        else if (y>= (heightScrollPane-(heightViewPort/2))) {
            // above 1 of scrollpane
            textWindow.setVvalue(1);
        }
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

        System.out.println("********VISIBLE NODES********");
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
