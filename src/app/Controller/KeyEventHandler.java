package app.Controller;

import app.Controller.Position.Positioner;
import app.Controller.Text.CreateTextOnCommand;
import app.Controller.Text.DeleteTextOnCommand;
import app.Controller.Text.TextRenderer;
import app.Model.*;
import app.Model.Command.Command;
import app.Model.Command.CommandList;
import app.Model.Cursor;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.HashMap;


public class KeyEventHandler implements EventHandler<KeyEvent> {

    public LinkedList linkedText;
    public HashMap<MyText, Node> hashMap;
    public HashMap<Integer, Node> hashMapIdx;
    public int fontSize;
    public String fontName;

    public Stage stage;
    public Scene scene;
    public Group group;
    public ScrollPane textWindow;
    public Positioner positioner;
    public Cursor cursor;

    public CommandList commandList;

    public TextRenderer textRenderer;
    private Boolean shiftStart;

    public KeyEventHandler(Stage stage, Scene scene, Group group, ScrollPane textWindow) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.textWindow = textWindow;
        this.hashMap = new HashMap<>();
        this.hashMapIdx = new HashMap<>(); // first node on a line
        this.linkedText = new LinkedList(hashMap, hashMapIdx);
        this.cursor = new Cursor(group);
        this.positioner = new Positioner(cursor, linkedText);

        this.fontSize = (int)cursor.getSampleLetter().getFont().getSize();
        this.fontName = cursor.getSampleLetter().getFont().getName();
        this.commandList = new CommandList();
        this.shiftStart = false;

        this.textRenderer = new TextRenderer(stage, scene, group, linkedText, positioner, cursor, textWindow, hashMap, hashMapIdx, commandList);
    }

    public void handleText() {
        textRenderer.renderText();
    }

    @Override
    public void handle(KeyEvent keyEvent) {

        /** ALPHABET KEYS **/
        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            String characterTyped = keyEvent.getCharacter();
            // Only include alphanumeric and special characters
            if (characterTyped.length() > 0 && characterTyped.matches("[\\w\\p{P}\\p{S}\\p{Space}]") &&
                    !characterTyped.matches("[\\r|\\n]") && !keyEvent.isControlDown()) {
                //textRenderer.textManipulator.createLetter(characterTyped, fontName, fontSize);

                /* Command for text creation */
                Command textCommand = new CreateTextOnCommand(textRenderer.textManipulator, characterTyped,"INSERT-LETTER");
                textCommand.execute();
                commandList.addCommand("INSERT-LETTER", textCommand);

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }
        }
        /** ARROW, CTRL, DELETE KEYS ETC. **/
        else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.UP) {
                handleShift(keyEvent);
                textRenderer.textManipulator.moveByLetter("UP", keyEvent);
                handleSelection(keyEvent);
                handleText();
                textRenderer.textManipulator.handleScrollView();
            }
            if (code == KeyCode.DOWN) {
                handleShift(keyEvent);
                textRenderer.textManipulator.moveByLetter("DOWN", keyEvent);
                handleSelection(keyEvent);
                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.LEFT) {
                handleShift(keyEvent);
                textRenderer.textManipulator.moveByLetter("LEFT", keyEvent);
                handleSelection(keyEvent);
                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.RIGHT) {
                handleShift(keyEvent);
                textRenderer.textManipulator.moveByLetter("RIGHT", keyEvent);
                handleSelection(keyEvent);
                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.BACK_SPACE) {
                /* Command for text deletion */
                Command textCommand = new DeleteTextOnCommand(textRenderer.textManipulator, "BACK_SPACE");
                textCommand.execute();
                commandList.addCommand("BACK_SPACE", textCommand);

                //textRenderer.textManipulator.deleteLetter("BACK_SPACE");
                //textRenderer.textManipulator.deleteSelection();

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.DELETE) {
                /* Command for text deletion */
                Command textCommand = new DeleteTextOnCommand(textRenderer.textManipulator, "DELETE");
                textCommand.execute();
                commandList.addCommand("DELETE", textCommand);

                //textRenderer.textManipulator.deleteLetter("DELETE");
                //textRenderer.textManipulator.deleteSelection();

                handleText();
                positioner.adjustUpdatedPosition();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.SHIFT) {
                if (textRenderer.textManipulator.pairSelection.isEmpty()) {
                    shiftStart = true;
                }
            }

            if (code == KeyCode.ENTER) {
                //textRenderer.textManipulator.createLetter("", fontName, fontSize);

                /* Command for text creation */
                Command textCommand = new CreateTextOnCommand(textRenderer.textManipulator, "","INSERT-LETTER");
                textCommand.execute();
                commandList.addCommand("INSERT-LETTER", textCommand);

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            if (code == KeyCode.ESCAPE || (code == KeyCode.F4 && keyEvent.isAltDown())) {
                System.exit(0);
            }

            // COPY
            if (code == KeyCode.C && keyEvent.isControlDown()) {
                textRenderer.textManipulator.copyText();
            }

            // PASTE
            if (code == KeyCode.V && keyEvent.isControlDown()) {
                //textRenderer.textManipulator.insertText();

                /* Command for text block insertion */
                String text = textRenderer.textManipulator.getTextFromClipboard();
                if (text == null) return;
                Command textCommand = new CreateTextOnCommand(textRenderer.textManipulator, text,"INSERT-SELECTION");
                textCommand.execute();
                commandList.addCommand("INSERT-SELECTION", textCommand);

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            // UNDO
            if ((code == KeyCode.Z && keyEvent.isControlDown()) && !keyEvent.isShiftDown()) {
                if (commandList.getCommand(false) == null) return;
                commandList.getCommand(false).undo();
                commandList.decreaseCurrentIndex();

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }

            // REDO
            if (code == KeyCode.Z && keyEvent.isControlDown() && keyEvent.isShiftDown()) {
                commandList.increaseCurrentIndex();
                if (commandList.getCommand(true) == null) return;
                commandList.getCommand(true).redo();

                handleText();
                textRenderer.textManipulator.handleScrollView();
            }


        }

        else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.SHIFT) {
                if (!textRenderer.textManipulator.pairSelection.isEmpty()) {
                    addSelection(1);
                }
                shiftStart = false;
            }


        }
        // RENDER NEW TEXT
        keyEvent.consume();
    }

    private void addSelection(int num) {
        if (positioner.getCurrentNode() == null || linkedText.isEmpty()) return;
        //System.out.println("Current node/add selection: " + positioner.getCurrentNode().getData());
        textRenderer.textManipulator.pairSelection.add(num, new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart()));
    }

    private void handleShift(KeyEvent keyEvent) {
        if (keyEvent.isShiftDown()) {
            if (shiftStart) {
                addSelection(0);
                shiftStart = false;
            }
        }
    }

    private void handleSelection(KeyEvent keyEvent) {
        if (keyEvent.isShiftDown()) {
            addSelection(1);
            textRenderer.textManipulator.moveSelection();
        }
        else {
            textRenderer.textManipulator.clipboard.unselectAll();
            textRenderer.textManipulator.pairSelection.clear();
        }
    }

}