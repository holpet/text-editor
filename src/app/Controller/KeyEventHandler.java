package app.Controller;

import app.Model.*;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.io.*;
import java.util.HashMap;


public class KeyEventHandler implements EventHandler<KeyEvent> {

    /** The Text to display on screen **/
    public LinkedList linkedText;
    public HashMap<MyText, Node> hashMap;
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
        this.hashMap = new HashMap<>();
        this.linkedText = new LinkedList(hashMap);

        this.cursor = new Cursor();
        group.getChildren().add(cursor.getStyleableNode());

        this.positioner = new Positioner(cursor, linkedText);
        this.textRenderer = new TextRenderer(scene, group, linkedText, positioner, cursor);

        this.fontSize = 12;
        this.fontName = "Verdana";

        // READ FILE
        //readTextFromFile("./txt/text_lorem_ipsum.txt");
    }

    private void readTextFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    createLetter( ( "" + line.charAt(i) ) );
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleText() {
        textRenderer.renderText();
    }

    public void createLetter(String letter) {
        MyText text = new MyText();
        text.setText(letter);
        text.setFont(Font.font(fontName, fontSize));
        // Insert node into linkedList, update position and save it in a hashMap
        linkedText.insertAt(text, positioner);
    }

    public void deleteLetter() {
        Node oldCurrentNode = positioner.getCurrentNode();
        if (oldCurrentNode != null) {
            linkedText.deleteAt(oldCurrentNode, positioner);
            group.getChildren().remove(oldCurrentNode.getData());
        }
    }

    @Override
    public void handle(KeyEvent keyEvent) {

        /** ALPHABET KEYS **/
        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            String characterTyped = keyEvent.getCharacter();
            if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                // Ignore control keys and backspace (non-zero length)
                createLetter(characterTyped);
            }

        /** ARROW, CTRL, DELETE KEYS ETC. **/
        } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // for arrow keys
            KeyCode code = keyEvent.getCode();

            if (code == KeyCode.UP) {
                fontSize += 5;
                //displayText.setFont(Font.font(fontName, fontSize));
            }
            if (code == KeyCode.DOWN) {
                fontSize = Math.max(0, fontSize - 5);
                //displayText.setFont(Font.font(fontName, fontSize));
            }

            if (code == KeyCode.BACK_SPACE) {
                deleteLetter();
            }
        }
        // RENDER NEW TEXT
        handleText();
        keyEvent.consume();
    }




}