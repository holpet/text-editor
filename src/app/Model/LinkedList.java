package app.Model;
import app.Controller.Position.Positioner;

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

        // Hashmap for faster access to individual nodes when manipulating the linked list
        this.hashMap = hashMap;
        this.hashMapIdx = hashMapIdx;
    }

    /** --------------- inserting & deleting letters --------------- **/
    // Cases to consider for inserting & deleting a proposed [NODE] at position of [CURRENT NODE]:
    // [CURRENT NODE] / letter is:
    // 1. At head (first letter of a text)
    // ----- cursor is on the left / right side
    // 2. At the beginning of a line (not just head)
    // ----- cursor is on the left / right side
    // 3. At tail (last letter of the text)
    // ----- cursor is on the right side
    // 4. Anywhere else
    // ----- cursor is on the right side

    // Edge cases:
    // - empty linked list vs. filled linked list
    // - there is no current letter chosen for insert / delete


    /** +++++++++++++++++++++++ INSERT LETTER +++++++++++++++++++++++++ **/

    // Insert letter into LL
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

    // Insert letter into LL + change current node position and cursor position
    public void insertAt(MyText data, Positioner positioner) {
        Node currentNode = positioner.getCurrentNode();
        Node node = insertAt_simplified(data, currentNode);
        positioner.setCurrentNode(node);
        positioner.setCursorIsAtStart(false);
        // System.out.println("cur node INS: " + positioner.getCurrentNode().getData());
    }

    /********************** UNDO / REDO **********************/
    public Node insertAt_UNDO_REDO(Node node, Positioner positioner) {
        if (isEmpty()) {
            sentinelHead.setNext(node);
            sentinelTail.setPrev(node);
            node.setNext(getSentinelTail());
            node.setPrev(getSentinelHead());
        }
        else {
            node.getPrev().setNext(node);
            if (isAtEnd(node.getNext())) {
                sentinelTail.setPrev(node);
            } else {
                node.getNext().setPrev(node);
            }
        }
        size++;
        hashMap.put(node.getData(), node);
        positioner.setCurrentNode(node);
        positioner.setCursorIsAtStart(false);
        return node;
    }


    /** +++++++++++++++++++++++ DELETE LETTER +++++++++++++++++++++++++ **/

    // Delete letter from LL
    public Node deleteAt_simplified(Node node) {
        if (isEmpty() || node == null) {
            return null;
        }
        Node old = node.getNext();
        node.getNext().setPrev(node.getPrev());
        node.getPrev().setNext(old);
        size--;
        hashMap.remove(node.getData());
        return node;
    }

    // [BACKSPACE] Delete letter from LL + change current node position and cursor position
    public Node deleteAt_BACKSPACE(Node node, Positioner positioner) {
        Boolean cursorAtStart = positioner.getCursorIsAtStart();

        // Don't delete anything if there is no letter to delete or if we're at the beginning of a text
        if (node == null) return null;
        if (isAtBeginning(node.getPrev()) && cursorAtStart) return null;

        // If we're at the beginning of a line and cursor is at start
        if (node.getData().getX() == 0 && cursorAtStart) {
            positioner.setCurrentNode(node.getPrev().getPrev());
            positioner.setCursorIsAtStart(false);
            return deleteAt_simplified(node.getPrev());
        }
        // If we're anywhere else
        else {
            positioner.setCurrentNode(node.getPrev());
            positioner.setCursorIsAtStart(false);
            return deleteAt_simplified(node);
        }
    }

    // [DELETE] Delete letter from LL + change current node position and cursor position
    public Node deleteAt_DELETE(Node node, Positioner positioner) {
        // Delete node after the current one and return it
        if (isEmpty() || node == null || (getLast() == node && !positioner.getCursorIsAtStart())) {
            return null;
        }
        // If we're at the beginning of a line and cursor is at start
        if (node.getData().getX() == 0 && positioner.getCursorIsAtStart()) {
            positioner.setCurrentNode(node.getNext());
            size--;
            hashMap.remove(node.getData());
            return deleteAt_simplified(node);
        }
        // If we're anywhere else
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



    /** +++++++++++++++++++++++ HELPER FUNCTIONS +++++++++++++++++++++++++ **/

    public void addFirst(MyText data) {
        Node node = new Node(data);
        if (isEmpty()) {
            sentinelHead.setNext(node);
            sentinelTail.setPrev(node);
            node.setNext(getSentinelTail());
            node.setPrev(getSentinelHead());
        }
        else {
            Node oldNext = sentinelHead.getNext();
            sentinelHead.setNext(node);
            node.setPrev(getSentinelHead());
            node.setNext(oldNext);
        }
    }

    public void clearLL() {
        sentinelHead.setNext(getSentinelTail());
        sentinelTail.setPrev(getSentinelHead());
        size = 0;
        hashMap.clear();
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
        return sentinelTail;
    }


    /** +++++++++++++++++++++++ PRINT ALL LETTERS +++++++++++++++++++++++++ **/

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

}
