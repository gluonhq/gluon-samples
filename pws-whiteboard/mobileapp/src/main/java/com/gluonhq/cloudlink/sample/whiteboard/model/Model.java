package com.gluonhq.cloudlink.sample.whiteboard.model;

import com.gluonhq.connect.GluonObservableList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Model {

    private final ObjectProperty<Item> activeItem = new SimpleObjectProperty<>();
    private GluonObservableList<Item> items;

    public ObjectProperty<Item> activeItem() {
        return activeItem;
    }

    public GluonObservableList<Item> getItems() {
        return items;
    }

    public void setItems(GluonObservableList<Item> items) {
        this.items = items;
    }
}
