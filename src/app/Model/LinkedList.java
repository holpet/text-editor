package app.Model;

import app.Controller.Positioner;

import java.util.HashMap;

public class LinkedList {

    //private Node head;
    private Node sentinelHead;
    private Node sentinelTail;
    private double size;
    public HashMap<MyText, Node> hashMap;

    public LinkedList(HashMap<MyText, Node> hashMap) {
        //this.head = null;
        this.sentinelHead = new Node(null);
        this.sentinelTail = new Node(null);
        sentinelHead.setNext(sentinelTail);
        sentinelTail.setPrev(sentinelHead);
        this.size = 0;
        this.hashMap = hashMap;
    }

//    public Node getHead() {
//        return head;
//    }
    /**
    public Node insertAtEnd(MyText data) {
        Node node = new Node(data);

        if (head == null) {
            head = node;
        }
        else {
            // Insert new data at the end of the list
            Node n = head;
            while (n.getNext() != null) {
                n = n.getNext();
            }
            n.setNext(node);
            n.getNext().setPrev(n);
        }
        hashMap.put(data, node);
        return node;
    }**/

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
            // Insert new data at current pos
            Node currentNode = positioner.getCurrentNode();
            //positioner.setCursorIsAtStart(false);
            if (positioner.getCursorIsAtStart()) {
                if (currentNode != null) {
                    currentNode.setPrev(node);
                    node.setNext(currentNode);
                    node.setPrev(getSentinelHead());
                    sentinelHead.setNext(node);
                }
                positioner.setCursorIsAtStart(false);
            }
            else {
                if (currentNode != null) {
                    Node oldCurNext = currentNode.getNext();
                    currentNode.setNext(node);
                    node.setPrev(currentNode);
                    node.setNext(oldCurNext);
                }
            }
        }
        positioner.setCurrentNode(node);
        size++;
        hashMap.put(data, node);
    }

    public void deleteAt(Node node, Positioner positioner) {
        // Delete current node and return the one that becomes the new current node
        if (isEmpty() || node == null) {
            return;
        }
        if (getFirst() == node) {
            if (positioner.getCursorIsAtStart()) {
                // Cursor is at the beginning, don't delete anything
                positioner.setCursorIsAtStart(true);
                return;
            }
            else {
                // Delete first node and set cursor at beginning
                sentinelHead.setNext(node.getNext());
                node.getNext().setPrev(getSentinelHead());
                positioner.setCursorIsAtStart(true);
                positioner.updatePosition();
                positioner.setCurrentNode(getFirst());
                size--;
                hashMap.remove(node.getData());
                return;
            }
        }
        if (node.getNext() != getSentinelTail()) {
            node.getNext().setPrev(node.getPrev());
        }
        if (node.getPrev() != getSentinelHead()) {
            node.getPrev().setNext(node.getNext());
        }
        positioner.setCurrentNode(node.getPrev());
        positioner.setCursorIsAtStart(false);
        positioner.updatePosition();
        size--;
        hashMap.remove(node.getData());
    }

    public void printAll() {
        Node temp = getFirst();
        if (temp != null) {
            while (temp != null) {
                System.out.println(temp.getData() + " added. ");
                temp = temp.getNext();
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
