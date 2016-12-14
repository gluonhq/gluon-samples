package com.gluonhq.cloudlink.sample.whiteboard.views;

import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.sample.whiteboard.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.service.Service;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

public class ItemCell extends ListCell<Item> {

    private final ListTile tile;
    private Item currentItem;
    private final DateTimeFormatter dateFormat;
    private final ChangeListener<String> noteChangeListener;

    public ItemCell(Service service, Consumer<Item> edit, Consumer<Item> remove) {
        tile = new ListTile();
        tile.setPrimaryGraphic(MaterialDesignIcon.DESCRIPTION.graphic());

        Button btnEdit = MaterialDesignIcon.EDIT.button(e -> edit.accept(currentItem));
        Button btnRemove = MaterialDesignIcon.DELETE.button(e -> remove.accept(currentItem));
        HBox buttonBar = new HBox(0, btnEdit, btnRemove);
        tile.setSecondaryGraphic(buttonBar);

        dateFormat = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy - HH:mm", Locale.ENGLISH);

        noteChangeListener = (obs, ov, nv) -> update();
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);

        if (currentItem != null) {
            currentItem.titleProperty().removeListener(noteChangeListener);
        }
        currentItem = item;
        if (currentItem != null) {
            currentItem.titleProperty().addListener(noteChangeListener);
        }

        if (!empty && item != null) {
            update();
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }

    private void update() {
        if (currentItem == null) {
            tile.textProperty().clear();
        } else {
            tile.textProperty().setAll(currentItem.getTitle(),
                    dateFormat.format(currentItem.getFormattedCreationDate()));
        }
    }
}
