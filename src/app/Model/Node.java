package app.Model;

public class Node extends javafx.scene.Node {
    private MyText data;
    private Node next;
    private Node prev;

    public Node(MyText data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    @Override
    public javafx.scene.Node getStyleableNode() {
        return this;
    }

    public MyText getData() {
        return data;
    }

    public void setData(MyText data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }
}
