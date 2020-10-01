package app.Controller.Text;

import app.Controller.Position.Positioner;
import app.Model.*;
import app.Model.Command.CommandList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class TextRenderer {

    public Stage stage;
    public Scene scene;
    public Group group;
    public LinkedList linkedText;
    public Positioner positioner;
    public Cursor cursor;
    public ScrollPane textWindow;
    public TextManipulator textManipulator;
    private final ArrayList<Node> dividers;
    private IndexUpdater indexUpdater;
    public HashMap<Integer, Node> hashMapIdx;
    public CommandList commandList;

    public TextRenderer(Stage stage, Scene scene, Group group, LinkedList linkedText, Positioner positioner,
                        Cursor cursor, ScrollPane textWindow, HashMap<MyText, Node> hashMap, HashMap<Integer, Node> hashMapIdx,
                        CommandList commandList) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.linkedText = linkedText;
        this.positioner = positioner;
        this.cursor = cursor;
        this.textWindow = textWindow;
        this.hashMapIdx = hashMapIdx;
        this.dividers = new ArrayList<>();
        this.indexUpdater = new IndexUpdater();
        this.commandList = commandList;

        this.textManipulator = new TextManipulator(stage, scene, group, linkedText, positioner, cursor, textWindow, hashMap, hashMapIdx, commandList);
    }

    public void renderText() {

        /** No text to render **/
        Node tmp = linkedText.getFirst();
        if (linkedText.isEmpty()) {
            return;
        }
        textManipulator.setLineCounter(0);
        hashMapIdx.clear();
        hashMapIdx.put(textManipulator.getLineCounter(), tmp);

        /** Start rendering text from the first letter **/
        // Initial variables
        int posX = 0;
        int posY = 0;
        double letterHeight = cursor.getSampleLetter().getLayoutBounds().getHeight();

        // Delete all dividers from linkedList and clear it for start
        for (Node node : dividers) {
            if (positioner.getCurrentNode() == node) {
                positioner.setCurrentNode(node.getPrev());
            }
            linkedText.deleteAt_simplified(node);
            textManipulator.deleteFromGroup(node);
        }
        dividers.clear();

        // Go through linkedList and add all the letters
        while (!linkedText.isAtEnd(tmp)) {

            // figure out letter size and position of a letter
            double letterWidth = tmp.getData().getLayoutBounds().getWidth();
            //double letterHeight = tmp.getData().getLayoutBounds().getHeight();

            tmp.getData().setX(posX);
            tmp.getData().setY(posY);
            tmp.getData().setTextOrigin(VPos.TOP);

            double nextLetterWidth = 0;
            if (!linkedText.isAtEnd(tmp.getNext())) {
                nextLetterWidth = tmp.getNext().getData().getLayoutBounds().getWidth();
            }

            if (!group.getChildren().contains(tmp.getData())) {
                textManipulator.addToGroup(tmp);
                // Set cursor position when writing
                positioner.updatePosition();
            }
            if (!linkedText.isAtEnd(tmp.getNext())) {
                tmp = tmp.getNext();
            }
            else {
                indexUpdater.startTheService();
                //linkedText.printAll();
                positioner.updatePosition();
                return;
            }
            NodeAndCoords nodeAndCoords = handleLines(tmp, nextLetterWidth, letterWidth, letterHeight, posX, posY);
            posX = nodeAndCoords.getCoordX();
            posY = nodeAndCoords.getCoordY();
            tmp = nodeAndCoords.getNode();
            positioner.updatePosition();
        }
    }

    private Boolean checkLineEnd(int newCoordX, double nextLetterWidth) {
        //int leftSpace = 52; // cca
        int padding = 20;
        double leftSpace = scene.getWidth() - textWindow.getViewportBounds().getWidth();
        if ((newCoordX + nextLetterWidth) >= (scene.getWidth() - leftSpace - padding)) {
            return true;
        }
        return false;
    }

    private NodeAndCoords handleLines(Node node, double nextLetterWidth, double letterWidth, double letterHeight, int initCoordX, int initCoordY) {
        int newCoordX = 0;
        int newCoordY = 0;
        NodeAndCoords nodeAndCoords = new NodeAndCoords(newCoordX, newCoordY, node);

        // Calculate coord X for the next letter
        newCoordX = initCoordX + (int)letterWidth;

        // If next letter is new line (ENTER KEY)
        if (node.getData().getText().equals("")) {
            nodeAndCoords.setNode(node);
            nodeAndCoords.setCoordX(0);
            nodeAndCoords.setCoordY((initCoordY + (int) letterHeight));
            //positioner.setCursorIsAtStart(true);
            textManipulator.setLineCounter((textManipulator.getLineCounter()+1));
            hashMapIdx.put(textManipulator.getLineCounter(), node);
            return nodeAndCoords;
        }

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
                    // Otherwise, search backwards until you find space/- indicating the beginning of the word
                    if (node.getPrev().getData().getText().equals(" ") || node.getPrev().getData().getText().equals("-")) {
                        nodeAndCoords.setNode(node);
                        nodeAndCoords.setCoordX(0);
                        nodeAndCoords.setCoordY((initCoordY + (int)letterHeight));
                        // Add line to the counter
                        textManipulator.setLineCounter((textManipulator.getLineCounter()+1));
                        hashMapIdx.put(textManipulator.getLineCounter(), node);
                        return nodeAndCoords;
                    }
                    else {
                        // Move onto the previous node
                        node = node.getPrev();
                        // If no space or "-" found on the line, simply move letter on the next line
                        if (node.getData().getY() != curNode.getData().getY() && node.getData().getX() == 0) {
                            break;
                        }
                    }
                }

                // One line contains letters with no space or divider -> divide letter with "-"
                MyText letter = textManipulator.createLetter_simplified("-", positioner.getCurrentNode().getData().getFont().getName(),
                        (int)positioner.getCurrentNode().getData().getFont().getSize());
                Node div = linkedText.insertAt_simplified(letter, curNode.getPrev().getPrev());
                div.getData().setX(initCoordX);
                div.getData().setY(initCoordY);
                div.getData().setTextOrigin(VPos.TOP);
                textManipulator.addToGroup(div);
                // Add line to the counter
                textManipulator.setLineCounter((textManipulator.getLineCounter()+1));
                hashMapIdx.put(textManipulator.getLineCounter(), node);

                // Add node in ArrayList "dividers"
                dividers.add(div);

                nodeAndCoords.setNode(div.getNext());
                nodeAndCoords.setCoordX(0);
                nodeAndCoords.setCoordY(initCoordY + (int)letterHeight);
                return nodeAndCoords;

            }
        }
        // If next letter fits on the line:
        else {
            // Add next letter on the line
            nodeAndCoords.setCoordX(newCoordX);
            nodeAndCoords.setCoordY(initCoordY);
            return nodeAndCoords;
        }
    }

    public class IndexUpdater extends Service<Boolean> {

        public IndexUpdater () {
            setOnSucceeded(s -> {
                //System.out.println("Index update succeeded...");
            });
            setOnFailed(fail -> {
                //System.out.println("Index update failed...");
            });
            setOnCancelled(cancelled->{
                //System.out.println("Index update cancelled...");
            });
        }

        public void startTheService(){
            if(!isRunning()){
                reset();
                start();
            }
        }

        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    linkedText.updateNodeIndex();
                    if (isCancelled()) {
                        return false;
                    }
                    return true;
                }
            };
        };
    }

}
