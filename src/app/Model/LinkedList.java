package app.Model;

import javafx.scene.Scene;
import javafx.scene.text.Text;

public class LinkedList {
    public Node head;
    public Positioner positioner;

    public LinkedList() {
        this.positioner = new Positioner();
        this.head = null;
    }

    public void insertAtEnd (Text data) {
        Node node = new Node(data);
        /** include start of file from Positioner **/

        if (head == null) {
            head = node;
            positioner.length++;
            positioner.currentNode = head;
            System.out.println("New head inserted.");
        }
        else {
            // Insert new data at the end of the list
            Node n = head;
            while (n.next != null) {
                positioner.length++;
                n = n.next;
            }
            n.next = node;
            n.next.prev = n;
            positioner.currentNode = n.next;
            System.out.println("New node inserted at END.");
        }
    }

    public void insertAtStart (Text data) {
        Node newHead = new Node(data);
        if (head == null) {
            head = newHead;
            positioner.length++;
            positioner.currentNode = head;
            System.out.println("New head inserted.");
        }
        else {
            Node oldHead = head;
            newHead.next = oldHead;
            head = newHead;
            newHead.next.prev = newHead;
            positioner.length++;
            positioner.currentNode = head;
            System.out.println("New node inserted at the START. New head created.");
        }
    }

    public void insertAtCurrent (Text data) {
        Node node = new Node(data);

        if (positioner.currentNode == null) {
            // If there is no current node (cursor) simply insert data at the end of the list
            insertAtEnd(data);
        }
        else {
            // Use current node (cursor) to insert new data at position
            if (positioner.currentNode.next == null) {
                positioner.currentNode.next = node;
                positioner.currentNode.next.prev = positioner.currentNode;
            }
            else {
                Node temp = positioner.currentNode.next;
                positioner.currentNode.next = node;
                positioner.currentNode.next.next = temp;
                positioner.currentNode.next.prev = positioner.currentNode;
            }
        }
    }

    public void showAll() {
        Node temp = head;
        if (temp != null) {
            while (temp != null) {
                System.out.println(temp.data + " added. ");
                temp = temp.next;
            }
        }
        else {
            System.out.println("No data has been added in a Node.");
        }
    }

}
