package app.Model;

public class Positioner {
    public int length; // length of linked list
    public Node currentNode; // pointer to current Node
    public int pos;

    // WINDOW PADDING
    private int paddX;
    private int paddY;

    public Positioner() {
        this.paddX = 0;
        this.paddY = 0;

        this.length = 0;
        this.currentNode = null;
        this.pos = 0;
    }

    public int getPaddX() {
        return paddX;
    }

    public void setPaddX(int paddX) {
        this.paddX = paddX;
    }

    public int getPaddY() {
        return paddY;
    }

    public void setPaddY(int paddY) {
        this.paddY = paddY;
    }





}
