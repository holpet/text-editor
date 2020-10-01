package app.Controller.Text;

import app.Model.Command.Command;
import app.Model.MyText;
import app.Model.Node;
import javafx.util.Pair;
import java.util.ArrayList;

public class DeleteTextOnCommand implements Command {
    private final TextManipulator textManipulator;
    private final String letter;
    private final String fontName;
    private final int fontSize;
    private final String pass;
    private MyText letterInsert;
    private MyText letterInsertSel;
    private Node currentNode;
    private Boolean currentCursorAtStart;
    private final Node initialNode;

    private Node undoneText;
    private ArrayList<Node> undoneTextArray;

    public DeleteTextOnCommand(TextManipulator textManipulator, String pass) {
        this.textManipulator = textManipulator;
        this.pass = pass;
        this.letter = null;
        this.fontName = textManipulator.cursor.getSampleLetter().getFont().getName();
        this.fontSize = (int)textManipulator.cursor.getSampleLetter().getFont().getSize();

        // Keeps track of inserted letters in command
        letterInsert = null; // filled with first letter of a selection or if only one letter inserted
        letterInsertSel = null; // filled with last letter of selection or remains null if only one letter inserted

        // Keeps track of current node & cursor in command
        initialNode = textManipulator.positioner.getCurrentNode();
        currentNode = textManipulator.positioner.getCurrentNode();
        currentCursorAtStart = textManipulator.positioner.getCursorIsAtStart();

        undoneText = null;
        undoneTextArray = new ArrayList<>();
    }

    private void deleteText(Boolean redo) {
        if (redo) {
            if (undoneTextArray == null) textManipulator.positioner.setCurrentNode(undoneText);
            else {
                textManipulator.pairSelection.add(0, new Pair<>(undoneTextArray.get(0), true));
                textManipulator.pairSelection.add(1, new Pair<>(undoneTextArray.get(undoneTextArray.size()-1), false));
                for (Node node : undoneTextArray) {
                    textManipulator.clipboard.select(node.getData(), true);
                }
            }
            textManipulator.positioner.setCursorIsAtStart(currentCursorAtStart);

            undoneText = textManipulator.deleteLetter(pass);
            undoneTextArray = textManipulator.deleteSelection();
        }
        else {
            undoneText = textManipulator.deleteLetter(pass);
            undoneTextArray = textManipulator.deleteSelection();
        }
        setCurrentNodeAndCursor(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart());
    }

    private void setCurrentNodeAndCursor(Node node, Boolean val) {
        currentNode = node;
        currentCursorAtStart = val;
    }

    private void setCurrentPositioner() {
        textManipulator.positioner.setCurrentNode(currentNode);
        textManipulator.positioner.setCursorIsAtStart(currentCursorAtStart);
    }

    @Override
    public void execute() {
        //textManipulator.clipboard.unselectAll();

        deleteText(false);
    }

    @Override
    public void redo() {
        textManipulator.clipboard.unselectAll();
        //setCurrentPositioner();

        deleteText(true);
    }

    @Override
    public void undo() {
        textManipulator.clipboard.unselectAll();
        textManipulator.positioner.setCurrentNode(currentNode);
        textManipulator.positioner.setCursorIsAtStart(currentCursorAtStart);

        // If we're inserting only one letter
        if (undoneTextArray == null) {
            textManipulator.linkedText.insertAt_UNDO_REDO(undoneText, textManipulator.positioner);
            textManipulator.clipboard.select(undoneText.getData(), true);
        }
        // If we're inserting a selection of letters
        else {
            for (Node node : undoneTextArray) {
                textManipulator.linkedText.insertAt_UNDO_REDO(node, textManipulator.positioner);
                textManipulator.clipboard.select(node.getData(), true);
            }
        }
        System.out.println("current node after undo: " + textManipulator.positioner.getCurrentNode().getData());
        textManipulator.positioner.setCurrentNode(initialNode);
        setCurrentNodeAndCursor(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart());


        /**
        if (pass.equals("BACK_SPACE")) {
            // Current letter is the one to be created
            textManipulator.positioner.setCursorIsAtStart(false);
            textManipulator.positioner.setCurrentNode(textManipulator.hashMap.get(letterInsert));
            undoneText = textManipulator.deleteLetter("BACK_SPACE");
        }
        else if (pass.equals("DELETE")) {
            Node text = textManipulator.hashMap.get(letterInsert);
            for (int i = 0; i < letter.length(); i++) {
                textManipulator.clipboard.select(text.getData(), true);
                text = text.getNext();
            }
            textManipulator.positioner.setCursorIsAtStart(false);
            textManipulator.positioner.setCurrentNode(textManipulator.hashMap.get(letterInsertSel));
            textManipulator.pairSelection.add(0, new Pair<>(textManipulator.hashMap.get(letterInsert), true));
            textManipulator.pairSelection.add(1, new Pair<>(textManipulator.hashMap.get(letterInsertSel), false));
            undoneTextArray = textManipulator.deleteSelection();
        }
        textManipulator.positioner.setCurrentNode(initialNode);
        textManipulator.positioner.setCursorIsAtStart(currentCursorAtStart);
        setCurrentNodeAndCursor(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart());
        System.out.println("cur node AFTER DELETE SEL: " + currentNode.getData());
         **/
    }

    @Override
    public String readCommand() {
        return ("" + pass + " - init node: " + initialNode.getData());
    }
}
