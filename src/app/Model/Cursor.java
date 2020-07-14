package app.Model;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Cursor {

    /**

    int textCenterX;
    int textCenterY;

    public Text displayText;
    public int fontSize = 12;
    private String fontName = "Verdana";

    public Scene scene;

    public KeyEventHandler(Scene scene, final Group group) {
        this.scene = scene;

        // Calculate with initial (preferred) size of Scene
        textCenterX = 800 / 2;
        textCenterY = (600-25) / 2;

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

        if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
            String characterTyped = keyEvent.getCharacter();
            if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                // Ignore control keys and backspace (non-zero length)
                displayText.setText(characterTyped);
                keyEvent.consume();
            }

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
        updateBBox();
    }

    public void updateBBox() {

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
    **/
}
