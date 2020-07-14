package app.Model;

public class Positioner {
    int length; // length of linked list
    Node currentNode; // pointer to current Node
    int pos;
    Boolean isStartOfFile;

    public Positioner() {
        this.length = 0;
        this.currentNode = null;
        this.pos = 0;
        this.isStartOfFile = true;
    }

    public Node getCurrentNode () {
        return currentNode;
    }


}
