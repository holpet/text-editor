package app.Model;

public final class NodeAndCoords {
    private int coordX;
    private int coordY;
    private Node node;

    public NodeAndCoords(int coordX, int coordY, Node node) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.node = node;
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public Node getNode() {
        return node;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
