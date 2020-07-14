package app.Controller;

import app.Model.LinkedList;
import app.Model.Node;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.security.Key;

public class KeyEventHandler implements EventHandler<KeyEvent> {

    /** Handle Keys that get pressed **/
    int textCenterX;
    int textCenterY;
    int textX;
    int textY;

    /** The Text to display on screen **/
    //public Text displayText;
    public LinkedList linkedText;
    public Text displayText;
    public int fontSize = 12;
    private String fontName = "Verdana";

    /** Other variables **/
    public Scene scene;
    public Group group;

    public KeyEventHandler(Scene scene, final Group group) {
        this.scene = scene;
        this.group = group;
        this.linkedText = new LinkedList();

        // Calculate with initial (preferred) size of Scene
        textX = 0;
        textY = 25;

        // Initialize empty text and add it to root so it will be displayed
        displayText = new Text(textCenterX, textCenterY, "P");


        // Set position to VPos.TOP - assigned Y pos, highest across all letters
        displayText.setTextOrigin(VPos.TOP);
        displayText.setFont(Font.font(fontName, fontSize));

        // Add all Nodes to the root to be displayed
        group.getChildren().add(displayText);

    }

    @Override
    public void handle(KeyEvent keyEvent) {

        /** ALPHABET KEYS **/
        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            String characterTyped = keyEvent.getCharacter();
            if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                // Ignore control keys and backspace (non-zero length)
                displayText.setText(characterTyped);

                Text letter = new Text();
                letter.setText(characterTyped);
                linkedText.insertAtEnd(letter);
                linkedText.showAll();

                keyEvent.consume();
            }

        /** ARROW KEYS **/
        } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            // for arrow keys
            KeyCode code = keyEvent.getCode();
            if (code == KeyCode.UP) {
                fontSize += 5;
                displayText.setFont(Font.font(fontName, fontSize));
            } else if (code == KeyCode.DOWN) {
                fontSize = Math.max(0, fontSize - 5);
                displayText.setFont(Font.font(fontName, fontSize));
            }
        }
        centerText();
    }


    public void renderText() {
        Node tmp = linkedText.head;
        if (tmp == null) {
            System.out.println("No text to be rendered.");
            return;
        }

        // Initial variables
        //int sceneWidth = resizeListener.currentWidth;
        //int sceneHeight = resizeListener.currentHeight;
        int posX = 0;
        int posY = 0;

        while (tmp != null) {
            // figure out letter size and position of a letter
            // leftText() // centerText() // rightText()
            double letterWidth = tmp.data.getLayoutBounds().getWidth();
            double letterHeight = tmp.data.getLayoutBounds().getHeight();
            tmp.data.setX(letterWidth);
            tmp.data.setY(letterHeight);
            tmp.data.toFront();
            group.getChildren().add(tmp.data);

            tmp = tmp.next;

            // solve end of line
            posX += letterWidth;
            //posY += letterHeight;

        }
    }

    public void centerText() {

        // Figure out the size of the current text
        double textHeight = displayText.getLayoutBounds().getHeight();
        double textWidth = displayText.getLayoutBounds().getWidth();

        // Calculate pos so the text will be centered on screen
        double textTop = textCenterY - textHeight / 2;
        double textLeft = textCenterX - textWidth / 2;

        // Repos text
        displayText.setX(textLeft);
        displayText.setY(textTop);

        // Text to appear in front of other objects
        displayText.toFront();

    }

    public void leftText() {
        // Figure out the size of the current text
        double textHeight = displayText.getLayoutBounds().getHeight();
        double textWidth = displayText.getLayoutBounds().getWidth();

        // Calculate pos so the text will be pushed to the left on screen
        //double textTop = textCenterY - textHeight / 2;
        //double textLeft = textCenterX - textWidth / 2;

        // Repos text
        //displayText.setX(textLeft);
        //displayText.setY(textTop);

        // Text to appear in front of other objects
        displayText.toFront();

    }

    public void rightText() {
        //TO DO
    }

}