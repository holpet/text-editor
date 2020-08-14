package app.Model;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Cursor extends Node {

    private final Rectangle cursor;
    private double curX;
    private double curY;
    private double curWidth;
    private double curHeight;
    private Boolean isRunning;
    private MyText sampleLetter;
    private final Thread cursorThread;

    public Cursor(Group group) {
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

        this.isRunning = true;
        cursorThread = new Thread(this::handleCursorThread);
        cursorThread.start();

        group.getChildren().add(cursor);
        cursor.toFront();
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
            }
            catch (InterruptedException e) {
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
        // (10%) more of cursor height than given letter size
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

    public MyText getSampleLetter() {
        return sampleLetter;
    }

    public void setSampleLetter(MyText sampleLetter) {
        this.sampleLetter = sampleLetter;
    }

}
