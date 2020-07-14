package app.Model;

import javafx.scene.text.Text;

public class Node {
    public Text data;
    public Node next;
    public Node prev;

    public Node(Text data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
