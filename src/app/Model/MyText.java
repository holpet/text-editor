package app.Model;

import app.Controller.Positioner;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MyText extends Text implements SelectableNode {

    public MyText() {
    }

    @Override
    public boolean requestSelection(boolean select) {
        return true;
    }

    @Override
    public void notifySelection(boolean select) {
        if (select) {
            this.setFill(Color.RED);
            //System.out.println("Letter is selected - RED.");
        }
        else {
            this.setFill(Color.BLACK);
            //System.out.println("Letter is not selected - BLACK.");
        }
    }

}
