package app.Model;

import app.Controller.KeyEventHandler;
import app.Controller.Positioner;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.util.HashMap;

public class LinkedList {

    private final Node sentinelHead;
    private final Node sentinelTail;
    private double size;
    public HashMap<MyText, Node> hashMap;
    public HashMap<Integer, Node> hashMapIdx;

    public LinkedList(HashMap<MyText, Node> hashMap, HashMap<Integer, Node> hashMapIdx) {
        MyText head = new MyText();
        head.setText("HEAD");
        MyText tail = new MyText();
        tail.setText("TAIL");
        this.sentinelHead = new Node(head);
        this.sentinelTail = new Node(tail);
        sentinelHead.setNext(sentinelTail);
        sentinelTail.setPrev(sentinelHead);
        this.size = 0;
        this.hashMap = hashMap;
        this.hashMapIdx = hashMapIdx;
    }

    public void insertAt(MyText data, Positioner positioner) {
        Node node = new Node(data);
        if (isEmpty()) {
            sentinelHead.setNext(node);
            sentinelTail.setPrev(node);
            node.setNext(getSentinelTail());
            node.setPrev(getSentinelHead());
            positioner.setCursorIsAtStart(false);
        }
        else {
            Node currentNode = positioner.getCurrentNode();
            if (currentNode == null) {
                return;
            }
            // Insert new data at current pos
            if (currentNode.getData().getX() == 0 && positioner.getCursorIsAtStart()) {
                Node oldCurPrev = currentNode.getPrev();
                currentNode.setPrev(node);
                node.setNext(currentNode);
                node.setPrev(oldCurPrev);
                oldCurPrev.setNext(node);
                positioner.setCursorIsAtStart(false);
            }
            else {
                Node oldCurNext = currentNode.getNext();
                currentNode.setNext(node);
                node.setPrev(currentNode);
                node.setNext(oldCurNext);
                oldCurNext.setPrev(node);
            }
        }
        positioner.setCurrentNode(node);
        size++;
        hashMap.put(data, node);
    }

    public Node deleteAt_BACKSPACE(Node node, Positioner positioner) {
        // Delete current node and return it
        if (isEmpty() || node == null) {
            return null;
        }
        if (getFirst() == node) {
            if (positioner.getCursorIsAtStart()) {
                // Cursor is at the beginning, don't delete anything
                positioner.setCursorIsAtStart(true);
                return null;
            }
            else {
                // Delete first node and set cursor at beginning
                if (isAtEnd(node.getNext())) {
                    sentinelHead.setNext(getSentinelTail());
                    sentinelTail.setPrev(getSentinelHead());
                }
                else {
                    sentinelHead.setNext(node.getNext());
                    node.getNext().setPrev(getSentinelHead());
                }
                positioner.setCursorIsAtStart(true);
                positioner.updatePosition();
                positioner.setCurrentNode(getFirst());
                size--;
                hashMap.remove(node.getData());
                return node;
            }
        }
        if (!isAtEnd(node.getNext())) {
            node.getNext().setPrev(node.getPrev());
        }
        if (!isAtBeginning(node.getPrev())) {
            node.getPrev().setNext(node.getNext());
        }
        positioner.setCurrentNode(node.getPrev());
        positioner.setCursorIsAtStart(false);
        positioner.updatePosition();
        size--;
        hashMap.remove(node.getData());
        return node;
    }

    public Node deleteAt_DELETE(Node node, Positioner positioner) {
        // Delete node after the current one and return it
        if (isEmpty() || node == null || (getLast() == node && !positioner.getCursorIsAtStart())) {
            return null;
        }

        if (isAtBeginning(node.getPrev()) && positioner.getCursorIsAtStart()) {
            sentinelHead.setNext(node.getNext());
            node.getNext().setPrev(getSentinelHead());
            if (!isAtEnd(node.getNext())) {
                positioner.setCursorIsAtStart(true);
                positioner.setCurrentNode(node.getNext());
            }
            else {
                positioner.setCursorIsAtStart(false);
                positioner.setCurrentNode(null);
            }
            size--;
            hashMap.remove(node.getData());
            return node;
        }
        Node toDelete = node.getNext();
        Node oldNext = node.getNext().getNext();
        if (isAtEnd(oldNext)) {
            sentinelTail.setPrev(node);
            node.setNext(getSentinelTail());
        }
        else {
            oldNext.setPrev(node);
            node.setNext(oldNext);
        }
        positioner.setCursorIsAtStart(false);
        size--;
        hashMap.remove(toDelete.getData());
        return toDelete;
    }

    public Node insertAt_simplified(MyText data, Node currentNode) {
        Node node = new Node(data);
        if (isEmpty()) {
            sentinelHead.setNext(node);
            sentinelTail.setPrev(node);
            node.setNext(getSentinelTail());
            node.setPrev(getSentinelHead());
        }
        else {
            Node oldNext = currentNode.getNext();
            currentNode.setNext(node);
            node.setPrev(currentNode);
            node.setNext(oldNext);
            if (isAtEnd(oldNext)) {
                sentinelTail.setPrev(node);
            } else {
                oldNext.setPrev(node);
            }
        }
        size++;
        hashMap.put(data, node);
        return node;
    }

    public Node deleteAt_simplified(Node node) {
        if (isEmpty() || node == null) {
            return null;
        }
        Node old = node.getNext();
        node.getNext().setPrev(node.getPrev());
        node.getPrev().setNext(old);

        //printAll();
        size--;
        hashMap.remove(node.getData());
        return node;
    }

    public void updateNodeIndex() {
        Node tmp = getFirst();
        int ctn = 0;
        if (!isEmpty()) {
            while (!isAtEnd(tmp)) {
                tmp.setIndex(ctn);
                ctn++;
                tmp = tmp.getNext();
            }
        }
    }

    public void printAll() {
        Node tmp = getFirst();
        System.out.println("New Print...");
        if (!isEmpty()) {
            while (!isAtEnd(tmp)) {
                System.out.println("PRINTING #" + tmp.getIndex() + ": " + tmp.getData());
                tmp = tmp.getNext();
            }
        }
        else {
            System.out.println("No data has been added in a Node.");
        }
    }

    public double getSize() {
        return size;
    }

    public Boolean isEmpty() {
        return size == 0;
    }

    public Node getFirst() {
        if (isEmpty()) {
            return null;
        }
        return sentinelHead.getNext();
    }

    public Node getLast() {
        if (isEmpty()) {
            return null;
        }
        return sentinelTail.getPrev();
    }

    public Boolean isAtEnd(Node node) {
        return getSentinelTail() == node;
    }

    public Boolean isAtBeginning(Node node) {
        return getSentinelHead() == node;
    }

    public Node getSentinelHead() {
        return sentinelHead;
    }

    public Node getSentinelTail() {
        return sentinelHead;
    }
}
