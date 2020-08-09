package app.Controller;

import app.Model.*;
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

    /** The Text to display on screen **/
    public LinkedList linkedText;
    public HashMap<MyText, Node> hashMap;
    public int fontSize;
    public String fontName;

    /** Other variables **/
    public Stage stage;
    public Scene scene;
    public Group group;
    public Positioner positioner;
    public TextRenderer textRenderer;
    public Cursor cursor;
    public ScrollPane textWindow;

    public KeyEventHandler(Stage stage, Scene scene, Group group, ScrollPane textWindow) {
        this.stage = stage;
        this.scene = scene;
        this.group = group;
        this.textWindow = textWindow;
        this.hashMap = new HashMap<>();
        this.linkedText = new LinkedList(hashMap);

        this.cursor = new Cursor(group);

        this.positioner = new Positioner(cursor, linkedText);
        this.textRenderer = new TextRenderer(stage, scene, group, linkedText, positioner, cursor, textWindow, hashMap);

        this.fontSize = 12;
        this.fontName = "Verdana";

        // READ FILE
        //readTextFromFile("./txt/text_lorem_ipsum.txt");
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
            if (characterTyped.length() > 0 && characterTyped.matches("[\\w\\p{P}\\p{S}\\p{Space}]")) {
                textRenderer.textManipulator.createLetter(characterTyped, fontName, fontSize);
                handleText();
            }
        }
        /** ARROW, CTRL, DELETE KEYS ETC. **/
        else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.UP) {
                textRenderer.textManipulator.moveByLetter("UP", keyEvent);
                handleSelection(keyEvent);
                handleText();
            }
            if (code == KeyCode.DOWN) {
                textRenderer.textManipulator.moveByLetter("DOWN", keyEvent);
                handleSelection(keyEvent);
                handleText();
            }

            if (code == KeyCode.LEFT) {
                textRenderer.textManipulator.moveByLetter("LEFT", keyEvent);
                handleSelection(keyEvent);
                handleText();
            }

            if (code == KeyCode.RIGHT) {
                textRenderer.textManipulator.moveByLetter("RIGHT", keyEvent);
                handleSelection(keyEvent);
                handleText();
            }

            if (code == KeyCode.BACK_SPACE) {
                textRenderer.textManipulator.deleteLetter("BACK_SPACE");
                textRenderer.textManipulator.deleteSelection();
                handleText();
            }

            if (code == KeyCode.DELETE) {
                textRenderer.textManipulator.deleteLetter("DELETE");
                textRenderer.textManipulator.deleteSelection();
                handleText();
            }

            if (code == KeyCode.SHIFT) {
                if (textRenderer.textManipulator.pairSelection.isEmpty()) {
                    addSelection(0);
                }
            }
        }

        else if (keyEvent.getEventType() == KeyEvent.KEY_RELEASED) {
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.SHIFT) {
                addSelection(1);
            }


        }

        // RENDER NEW TEXT
        keyEvent.consume();
    }

    private void addSelection(int num) {
        //textRenderer.textManipulator.selection.add(num, positioner.getCurrentNode());
        textRenderer.textManipulator.pairSelection.add(num, new Pair<>(positioner.getCurrentNode(), positioner.getCursorIsAtStart()));
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