package com.gluonhq.demos.tictactoe;

import com.sun.javafx.geom.Path2D;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.*;


public class BoardCell extends StackPane {

    enum Value {

        EMPTY(0) {
            @Override
            public Collection<Shape> createSymbol(BoardCell cell) {
                return Collections.emptyList();
            }
        },

        COMPUTER(1){
            @Override
            public Collection<Shape> createSymbol(BoardCell cell) {
                final DoubleBinding padding = cell.widthProperty().multiply(.15);

                Line l1 = new Line(20, 20, cell.getWidth() - 20, cell.getHeight() - 20);
                l1.setMouseTransparent(true);
                l1.getStyleClass().add("board-symbol");
                l1.endXProperty().bind(cell.widthProperty().subtract(padding));
                l1.endYProperty().bind(cell.heightProperty().subtract(padding));

                Line l2 = new Line(20, cell.getHeight() - 20, cell.getWidth() - 20, 20);
                l2.setMouseTransparent(true);
                l2.getStyleClass().add("board-symbol");
                l2.endXProperty().bind(cell.widthProperty().subtract(padding));
                l2.startYProperty().bind(cell.heightProperty().subtract(padding));

                return Arrays.asList(l1,l2);
            }
        },

        PLAYER(-1) {
            @Override
            public Collection<Shape> createSymbol(BoardCell cell) {
                Circle c = new Circle();
                c.setMouseTransparent(true);
                c.getStyleClass().add("board-symbol");
                final DoubleBinding padding = cell.widthProperty().multiply(.15);
                c.radiusProperty().bind(cell.widthProperty().divide(2).subtract(padding));
                return Arrays.asList(c);
            }
        };

        private int value;

        Value( int value ) {
            this.value = value;
        }

        public int getId() {
            return value;
        }

        public static Value fromId( int id ) {
            for( Value v: values()) {
                if ( v.getId() == id ) return v;
            }
            return EMPTY;
        }

        public abstract Collection<Shape> createSymbol( BoardCell cell );
    }

    private static String REGULAR_STYLE = "board-cell";
    private static String SELECTED_STYLE = "board-cell-selected";

    private Map<Value, Collection<Shape>> symbols = new HashMap<>();

    public BoardCell() {
        getStyleClass().add("board-cell");
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        updateSymbol();
    }

    // valueProperty
    private final ObjectProperty<Value> valueProperty = new SimpleObjectProperty<Value>(this, "value", Value.EMPTY){
        @Override
        public void set(Value newValue) {
            super.set(newValue);
            if ( newValue == Value.EMPTY) {
                setSelected(false);
            }
            updateSymbol();
        }
    };
    public final ObjectProperty<Value> valueProperty() {
       return valueProperty;
    }
    public final Value getValue() {
       return valueProperty.get();
    }
    public final void setValue(Value value) {
        valueProperty.set(value);
    }

    public boolean isEmpty() {
        return getValue() == Value.EMPTY;
    }

    private void updateSymbol() {
        Value currentValue = getValue();
        Collection<Shape> symbol = symbols.get(currentValue);
        if ( symbol == null ) {
            symbol = currentValue.createSymbol(this);
            symbols.put(currentValue,symbol);
        }
        getChildren().setAll(symbol);

    }

    // selectedProperty
    private final BooleanProperty selectedProperty = new SimpleBooleanProperty(this, "selected") {
        @Override
        public void set(boolean newValue) {
            super.set(newValue);
            Platform.runLater( () -> {
                if (newValue) {
                    getStyleClass().remove(REGULAR_STYLE);
                    getStyleClass().add(SELECTED_STYLE);
                } else {
                    getStyleClass().add(REGULAR_STYLE);
                    getStyleClass().remove(SELECTED_STYLE);
                }
            });
        }
    };
    public final BooleanProperty selectedProperty() {
       return selectedProperty;
    }
    public final boolean isSelected() {
       return selectedProperty.get();
    }
    public final void setSelected(boolean value) {
        selectedProperty.set(value);
    }

}
