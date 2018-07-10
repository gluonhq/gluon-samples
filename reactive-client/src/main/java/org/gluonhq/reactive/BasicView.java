package org.gluonhq.reactive;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class BasicView extends View {

    private Pane shapeCanvas = new Pane();
    private ObjectProperty<Shape> selectedShapeProperty = new SimpleObjectProperty<>(null);

    BasicView() {

        setCenter(shapeCanvas);

        // clear selection on canvas click
        JavaFxObservable.eventsOf(shapeCanvas, MouseEvent.MOUSE_CLICKED)
                        .subscribe( e-> clearSelection());
    }

    @Override
    protected void updateAppBar(AppBar appBar) {

        Button removeButton = MaterialDesignIcon.DELETE.button(this::removeSelected);
        removeButton.setDisable(true);

        selectedShapeProperty.addListener( (o, oldShape, newShape) -> {
            updateShapeAppearance( oldShape, false );
            updateShapeAppearance( newShape, true );
            removeButton.setDisable( newShape == null );
        });

        appBar.setTitleText("Reactive Sample");
        appBar.getActionItems().add(MaterialDesignIcon.ADD_BOX.button(this::addBox) );
        appBar.getActionItems().add(MaterialDesignIcon.ADD_CIRCLE.button(this::addCircle));
        appBar.getActionItems().add(removeButton);
        appBar.getActionItems().add(MaterialDesignIcon.CLEAR_ALL.button(this::clearAllShapes));
    }

    private static void updateShapeAppearance( Shape shape, boolean selected ) {
        if ( shape == null ) return;

        shape.setFill(selected ? Color.LIGHTGREEN : Color.LIGHTBLUE);
        shape.setStroke(Color.DARKGRAY);
        shape.setStrokeWidth(2.5);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setOffsetX(3.0);
        shadow.setColor(Color.GRAY);
        shape.setEffect(shadow);
    }

    private void addBox(ActionEvent event) {
        shapeCanvas.getChildren().add( new RectShape(this).getRootShape());
    }

    private void addCircle(ActionEvent event) {
        shapeCanvas.getChildren().add( new CircleShape(this).getRootShape());
    }

    private void removeSelected(ActionEvent event) {

        Optional.ofNullable(selectedShapeProperty.get())
                .ifPresent( node -> {
                    clearSelection();
                    shapeCanvas.getChildren().remove(node);
                });

    }

    private void clearAllShapes(ActionEvent event) {
        shapeCanvas.getChildren().clear();
        clearSelection();
    }

    private void clearSelection() {
        select(null);
    }

    void select( Shape shape ) {
        if ( shape != selectedShapeProperty.get() ) {
            selectedShapeProperty.set(shape);
        }
    }

    private Random random = new Random();

    double getRandomX() {
        return random.nextDouble() * this.getWidth();
    }

    double getRandomY() {
        return random.nextDouble() * this.getHeight();
    }




}

class BaseShape {

    private Shape rootShape;
    private Point2D delta;
    private BasicView view;

    BaseShape( BasicView view, Supplier<Shape> buildRootShape) {

        this.view = view;
        rootShape = buildRootShape.get();
        view.select(rootShape);

        // select shape on click
        Disposable disposable = JavaFxObservable.eventsOf(rootShape, MouseEvent.MOUSE_CLICKED)
                .subscribe( e -> {
                    view.select(rootShape);
                    e.consume();
                });


        // dispose "listeners" when shape is removed from the scene
        JavaFxObservable.changesOf(rootShape.sceneProperty())
                .filter( scene -> scene == null )
                .subscribe( s -> disposable.dispose());


        // calculate delta between shape location and initial mouse position on mouse pressed
        JavaFxObservable
                .eventsOf( rootShape, MouseEvent.MOUSE_PRESSED )
                .map( e -> new Point2D( e.getSceneX(), e.getSceneY()))
                .subscribe( p -> {
                    view.select(rootShape);
                    Bounds bounds = rootShape.localToScene(rootShape.getLayoutBounds());
                    delta =  p.subtract( new Point2D(bounds.getMinX(), bounds.getMinY()) );
                });

        // User current mouse position and delta to recalculate and set new shape location on mouse dragged
        JavaFxObservable
                .eventsOf( rootShape, MouseDragEvent.MOUSE_DRAGGED )
                .map( e -> rootShape.sceneToLocal(e.getSceneX() - delta.getX(),  e.getSceneY() - delta.getY()))
                .map( p -> rootShape.localToParent(p))
                .subscribe( p -> rootShape.relocate(  p.getX(), p.getY()));

    }

    Shape getRootShape() {
        return rootShape;
    }

}


class RectShape extends BaseShape {

    RectShape(BasicView view) {
        super( view, () -> new Rectangle( view.getRandomX(), view.getRandomY(), 100, 100 ));
    }

}

class CircleShape extends BaseShape {

    CircleShape(BasicView view) {
        super( view, () ->  new Circle( view.getRandomX(), view.getRandomY(), 50 ));
    }

}



