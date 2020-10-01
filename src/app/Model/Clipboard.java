package app.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {

    private ObservableList<SelectableNode> selectedItems = FXCollections.observableArrayList();

    public ObservableList<SelectableNode> getSelectedItems() {
        return selectedItems;
    }

    public boolean select(SelectableNode node, boolean selected) {
        if(node.requestSelection(selected)) {
            if (selected) {
                selectedItems.add(node);
            } else {
                selectedItems.remove(node);
            }
            node.notifySelection(selected);
            return true;
        } else {
            return false;
        }
    }

    public void unselect(SelectableNode node) {
        select(node, false);
    }

    public void unselectAll() {
        List<SelectableNode> unselectList = new ArrayList<>();
        unselectList.addAll(selectedItems);

        for (SelectableNode sN : unselectList) {
            select(sN, false);
        }
    }

}
