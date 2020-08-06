package app.Controller;

import app.Model.*;
import app.Model.Cursor;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.awt.*;
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

        this.cursor = new Cursor();
        group.getChildren().add(cursor.getStyleableNode());

        this.positioner = new Positioner(cursor, linkedText);
        positioner.notifyCurrentNode();
        this.textRenderer = new TextRenderer(stage, scene, group, linkedText, positioner, cursor, textWindow);

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
                textRenderer.createLetter(characterTyped, fontName, fontSize);
                handleText();
            }

        /** ARROW, CTRL, DELETE KEYS ETC. **/
        } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.UP) {
                textRenderer.moveByLetter("UP");
                handleText();
                //textRenderer.handleScrollView("UP");
            }
            if (code == KeyCode.DOWN) {
                textRenderer.moveByLetter("DOWN");
                handleText();
                //textRenderer.handleScrollView("DOWN");
            }

            if (code == KeyCode.LEFT) {
                textRenderer.moveByLetter("LEFT");
                handleText();
            }

            if (code == KeyCode.RIGHT) {
                textRenderer.moveByLetter("RIGHT");
                handleText();
            }

            if (code == KeyCode.BACK_SPACE) {
                textRenderer.deleteLetter("BACK_SPACE");
                handleText();
            }

            if (code == KeyCode.DELETE) {
                textRenderer.deleteLetter("DELETE");
                handleText();
            }
        }
        // RENDER NEW TEXT
        keyEvent.consume();
    }


}