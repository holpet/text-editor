package app.Model;

import app.Controller.Positioner;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;

public class Cursor extends Node {

    private Rectangle cursor;
    private double curX;
    private double curY;
    private double curWidth;
    private double curHeight;
    private Boolean isRunning;
    private MyText sampleLetter;
    private Thread cursorThread;

    public Cursor() {
        this.curX = 0;
        this.curY = 0;

        // Set original text properties and use sample letter to set cursor
        int fontSize = 12;
        String fontName = "Verdana";
        MyText sampleLetter = new MyText();
        sampleLetter.setText("A");
        sampleLetter.setFont(Font.font(fontName, fontSize));
        this.sampleLetter = sampleLetter;

        // Set cursor EndX, EndY
        setCursorPosAndSize();

        this.cursor = new Rectangle(curX, curY, curWidth, curHeight);
        cursor.setFill(Color.BLACK);
        cursor.toFront();

        this.isRunning = true;
        cursorThread = new Thread(this::handleCursorThread);
        cursorThread.start();

    }

    @Override
    public Node getStyleableNode() {
        return cursor;
    }


    public void startCursorBlinking() {
        cursor.setFill(Color.BLACK);
        isRunning = true;
    }

    public void stopCursorBlinking() {
        isRunning = false;
    }

    private void handleCursorThread() {
        while (isRunning) {
            Platform.runLater(()->{
                // Every 0.5s switch color of the cursor from black to transparent and vice versa
                if (cursor.getFill().equals(Color.BLACK)) {
                    cursor.setFill(Color.TRANSPARENT);
                }
                else {
                    cursor.setFill(Color.BLACK);
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Cursor thread couldn't sleep.");
            }
        }
    }

    public void setCursorPosAndSize() {
        double letterWidth = sampleLetter.getLayoutBounds().getWidth();
        double letterHeight = sampleLetter.getLayoutBounds().getHeight();

        // 10% of letter width is cursor width
        long newLetterWidth = Math.round((letterWidth * 0.1));
        // 10% more of cursor height than given letter size
        long newLetterHeight = Math.round(letterHeight);

        setCurWidth(newLetterWidth);
        setCurHeight(newLetterHeight);

        double diffWidth = newLetterWidth - letterWidth;
        double diffHeight = newLetterHeight - letterHeight;
        //setCurX( (getCurX() - (diffWidth/2)) );
        setCurY( (getCurY() + (diffHeight/2)) );
    }

    public void changeCursorPos(double curX, double curY) {
        setCurX(curX);
        setCurY(curY);
        setCursorPosAndSize();
        cursor.setX(getCurX());
        cursor.setY(getCurY());
        stopCursorBlinking();
        startCursorBlinking();
    }

    public void changeCursorSize(MyText newSampleText) {
        // based on new font / size of letters
        setSampleLetter(newSampleText);
        setCursorPosAndSize();
    }


    /** GETTERS & SETTERS **/

    public double getCurX() {
        return curX;
    }

    public void setCurX(double curX) {
        this.curX = curX;
    }

    public double getCurY() {
        return curY;
    }

    public void setCurY(double curY) {
        this.curY = curY;
    }

    public double getCurWidth() {
        return curWidth;
    }

    public void setCurWidth(double curWidth) {
        this.curWidth = curWidth;
    }

    public double getCurHeight() {
        return curHeight;
    }

    public void setCurHeight(double curHeight) {
        this.curHeight = curHeight;
    }

    public Text getSampleLetter() {
        return sampleLetter;
    }

    public void setSampleLetter(MyText sampleLetter) {
        this.sampleLetter = sampleLetter;
    }

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
