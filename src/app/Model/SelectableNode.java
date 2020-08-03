package app.Model;

public interface SelectableNode {
    public boolean requestSelection(boolean select);

    public void notifySelection(boolean select);
}
