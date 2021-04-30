/*
 * Copyright (c) 2021, Gluon
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
package com.gluonhq.samples.alarm.views;

import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.alarm.model.Event;
import com.gluonhq.samples.alarm.service.Service;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventCell extends ListCell<Event> {

    private final Service service;
    private final ListTile tile;
    private final HBox box;
    private Event currentItem;
    private final DateTimeFormatter dateFormat;

    EventCell(Service service) {
        this.service = service;
        tile = new ListTile();
        tile.setPrimaryGraphic(MaterialDesignIcon.ALARM.graphic());
        dateFormat = DateTimeFormatter.ofPattern("EEE, MMM dd - HH:mm", Locale.ENGLISH);
        Button btnRemove = MaterialDesignIcon.DELETE.button(e -> service.removeEvent(currentItem));
        box = new HBox(btnRemove);
        box.setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    public void updateItem(Event item, boolean empty) {
        super.updateItem(item, empty);
        currentItem = item;
        if (!empty && item != null) {
            tile.setTextLine(0, item.getTitle());
            tile.setTextLine(1, item.getText());
            tile.setTextLine(2, dateFormat.format(item.getCreationZonedDateTime()) + " :: " + dateFormat.format(item.getEventZonedDateTime()));
            if (item.getEventZonedDateTime().isAfter(ZonedDateTime.now())) {
                tile.setSecondaryGraphic(box);
            } else {
                if (item.isDelivered()) {
                    Node delivered = MaterialDesignIcon.DONE.graphic();
                    delivered.getStyleClass().add("delivered");
                    tile.setSecondaryGraphic(delivered);
                } else {
                    Node failed = MaterialDesignIcon.ERROR.graphic();
                    failed.getStyleClass().add("failed");
                    tile.setSecondaryGraphic(failed);
                }
            }
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }
}
