package app.Controller.Text;

import app.Model.Command.Command;
import app.Model.MyText;
import app.Model.Node;
import javafx.util.Pair;
import java.util.ArrayList;


public class CreateTextOnCommand implements Command {
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

    public CreateTextOnCommand(TextManipulator textManipulator, String letter, String pass) {
        this.textManipulator = textManipulator;
        this.pass = pass;
        this.letter = letter;
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


    private void insertSelection(Boolean redo) {
        String word = letter;
        if (word == null) return;
        textManipulator.toInsertSelection = true;
        textManipulator.pairSelection.add(0, new Pair<>(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart()));

        // Normal insert
        if (!redo) {
            MyText txt = null;
            for (int i = 0; i < word.length(); i++) {
                String letter = Character.toString(word.charAt(i));
                if (word.charAt(i) == '\n') letter = "";
                txt = textManipulator.createLetter(letter, fontName, fontSize);
                textManipulator.clipboard.select(txt, true);
                if (i == 0) letterInsert = txt;
            }
            letterInsertSel = txt;
        }
        // Redo insert
        else {
            MyText undoneArText = null;
            for (int i = 0; i < undoneTextArray.size(); i++) {
                undoneArText = undoneTextArray.get(i).getData();
                textManipulator.linkedText.insertAt_UNDO_REDO(undoneTextArray.get(i), textManipulator.positioner);
                textManipulator.clipboard.select(undoneArText, true);
                if (i == 0) letterInsert = undoneArText;
            }
            letterInsertSel = undoneArText;
        }

        textManipulator.toInsertSelection = false;
        textManipulator.pairSelection.add(1, new Pair<>(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart()));

        setCurrentNodeAndCursor(textManipulator.positioner.getCurrentNode(), textManipulator.positioner.getCursorIsAtStart());
    }

    private void insertLetter(Boolean redo) {
        if (redo) {
            textManipulator.linkedText.insertAt_UNDO_REDO(undoneText, textManipulator.positioner);
            textManipulator.clipboard.select(undoneText.getData(), true);
        }
        else {
            MyText txt = textManipulator.createLetter(letter, fontName, fontSize);
            letterInsert = txt;
            letterInsertSel = null;
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
        textManipulator.clipboard.unselectAll();
        if (pass.equals("INSERT-LETTER")) {
            insertLetter(false);
        }
        else if (pass.equals("INSERT-SELECTION")) {
            insertSelection(false);
        }
    }

    @Override
    public void redo() {
        textManipulator.clipboard.unselectAll();
        setCurrentPositioner();
        if (pass.equals("INSERT-LETTER")) {
            insertLetter(true);
        }
        else if (pass.equals("INSERT-SELECTION")) {
            insertSelection(true);
        }
    }

    @Override
    public void undo() {
        textManipulator.clipboard.unselectAll();
        // No letter/selection has been inserted through this command
        if (letterInsert == null) return;

        if (pass.equals("INSERT-LETTER")) {
            // Current letter is the one to be deleted
            textManipulator.positioner.setCursorIsAtStart(false);
            textManipulator.positioner.setCurrentNode(textManipulator.hashMap.get(letterInsert));
            undoneText = textManipulator.deleteLetter("BACK_SPACE");
        }
        else if (pass.equals("INSERT-SELECTION")) {
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
    }

    @Override
    public String readCommand() {
        return ("" + pass + " - init node: " + initialNode.getData());
    }
}
