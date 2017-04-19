/*
 * Copyright (c) 2016, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
