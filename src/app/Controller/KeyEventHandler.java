package app.Controller;

import app.Model.Cursor;
import app.Model.LinkedList;
import app.Model.Node;
import app.Model.Positioner;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class KeyEventHandler implements EventHandler<KeyEvent> {

    /** Screen properties **/
    int currentWidth;
    int currentHeight;

    /** The Text to display on screen **/
    public LinkedList linkedText;
    public int fontSize;
    public String fontName;

    /** Other variables **/
    public Scene scene;
    public Group group;
    public Positioner positioner;
    public TextRenderer textRenderer;
    public Cursor cursor;

    public KeyEventHandler(Scene scene, Group group) {
        this.scene = scene;
        this.group = group;
        this.positioner = new Positioner();
        this.linkedText = new LinkedList();

        // Set original text properties and use sample letter to set cursor
        fontSize = 12;
        fontName = "Verdana";
        Text sampleLetter = new Text("A");
        sampleLetter.setFont(Font.font(fontName, fontSize));

        this.cursor = new Cursor(0, 0, sampleLetter);
        group.getChildren().add(cursor.getStyleableNode());

        this.textRenderer = new TextRenderer(scene, group, linkedText, positioner, cursor);

        // Set current width and height
        currentWidth = scene.widthProperty().intValue();
        currentHeight = scene.heightProperty().intValue();

    }

    @Override
    public void handle(KeyEvent keyEvent) {

        /** ALPHABET KEYS **/
        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            String characterTyped = keyEvent.getCharacter();
            if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                // Ignore control keys and backspace (non-zero length)

                // SET TEXT PROPERTIES
                Text letter = new Text();
                letter.setText(characterTyped);
                letter.setFont(Font.font(fontName, fontSize));

                // INSERT IT INTO A LINKED LIST
                linkedText.insertAtEnd(letter);
                //linkedText.showAll();
            }

        /** ARROW, CTRL, DELETE KEYS ETC. **/
        } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // for arrow keys
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.CAPS) {
                //System.out.println("Caps pressed.");
            }

            if (code == KeyCode.UP) {
                fontSize += 5;
                //displayText.setFont(Font.font(fontName, fontSize));
            } else if (code == KeyCode.DOWN) {
                fontSize = Math.max(0, fontSize - 5);
                //displayText.setFont(Font.font(fontName, fontSize));
            }
            else if (code == KeyCode.BACK_SPACE) {
                // DELETE LETTER
                // find out last member of linked list
                Node last = linkedText.getLast();
                linkedText.deleteAtCurrent(last);
                //linkedText.showAll();
                if (last != null) {
                    textRenderer.deleteLetter(last.data);
                }
            }
        }
        // RENDER NEW TEXT
        textRenderer.renderText();
        keyEvent.consume();
    }




}