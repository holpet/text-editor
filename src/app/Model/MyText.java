package app.Model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static javafx.scene.paint.Color.rgb;

public class MyText extends Text implements SelectableNode {

    private final Rectangle background;

    public MyText() {
        double x = this.getBoundsInParent().getMinX();
        double y = this.getBoundsInParent().getMinY();
        double width = this.getLayoutBounds().getWidth();
        double height = this.getLayoutBounds().getHeight();
        this.background = new Rectangle(x, y, width, height);
        background.setFill(Color.TRANSPARENT);
        selectAndNotify();
    }

    public Rectangle getBackground() {
        return background;
    }

    private void selectAndNotify() {
        ChangeListener<Bounds> changeListener = new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
                background.setX(newBounds.getMinX());
                background.setY(newBounds.getMinY());
                background.setWidth(newBounds.getWidth());
                background.setHeight(newBounds.getHeight());
                //System.out.println("Bounds changed..." + oldBounds + " ...new bounds: " + newBounds);
            }
        };
        this.boundsInParentProperty().addListener(changeListener);
    }

    @Override
    public boolean requestSelection(boolean select) {
        return true;
    }

    @Override
    public void notifySelection(boolean select) {
        if (select) {
            this.setFill(Color.WHITE);
            background.setFill(rgb(110, 173, 196));

        }
        else {
            this.setFill(Color.BLACK);
            background.setFill(Color.TRANSPARENT);
        }
    }

    @Override
    public Node getStyleableNode() {
        return this;
    }
}

