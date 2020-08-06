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

    public boolean select(SelectableNode n, boolean selected) {
        if(n.requestSelection(selected)) {
            if (selected) {
                selectedItems.add(n);
            } else {
                selectedItems.remove(n);
            }
            n.notifySelection(selected);
            return true;
        } else {
            return false;
        }
    }

    public void unselectAll() {
        List<SelectableNode> unselectList = new ArrayList<>();
        unselectList.addAll(selectedItems);

        for (SelectableNode sN : unselectList) {
            select(sN, false);
        }
    }

}
