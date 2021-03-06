package app.Model;

public class Node extends javafx.scene.Node {
    private MyText data;
    private Node next;
    private Node prev;
    private int index;

    public Node(MyText data) {
        this.data = data;
        this.next = null;
        this.prev = null;
        this.index = 0;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
