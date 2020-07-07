package app.Model;

public class TextNode {
    char data;
    TextNode next;
    TextNode prev;

    public TextNode (char data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    private void setNext (char data, TextNode prev) {
        next.data = data;
        this.prev = prev;
    }
}
