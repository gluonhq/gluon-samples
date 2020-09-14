/*
 * Copyright (c) 2016, 2020, Gluon
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
package com.gluonhq.samples.notes.views;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.notes.model.Note;
import com.gluonhq.samples.notes.model.Settings;
import com.gluonhq.samples.notes.service.Service;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class NoteCell extends CharmListCell<Note> {

    private final ListTile tile;
    private Note currentItem;
    private final DateTimeFormatter dateFormat;
    
    private Settings settings;
    private int fontSize = -1;

    NoteCell(Service service, Consumer<Note> edit, Consumer<Note> remove) {
        
        tile = new ListTile();
        tile.setPrimaryGraphic(MaterialDesignIcon.DESCRIPTION.graphic());
        
        Button btnEdit = MaterialDesignIcon.EDIT.button(e -> edit.accept(currentItem));
        Button btnRemove = MaterialDesignIcon.DELETE.button(e -> remove.accept(currentItem));
        HBox buttonBar = new HBox(0, btnEdit, btnRemove);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        tile.setSecondaryGraphic(buttonBar);
        
        dateFormat = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy - HH:mm", Locale.ENGLISH);
        
        service.settingsProperty().addListener((obs, ov, nv) -> {
            settings = nv;
            update();
        });
        settings = service.settingsProperty().get();
        update();
    }

    @Override
    public void updateItem(Note item, boolean empty) {
        super.updateItem(item, empty);
        currentItem = item;
        if (!empty && item != null) {
            update();
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }
    
    private void update() {
        if (currentItem != null) {
            tile.textProperty().setAll(currentItem.getTitle(), 
                                       getSecondLine(currentItem.getText()));
            if (settings.isShowDate()) {
                tile.textProperty().add(dateFormat.format(currentItem.getCreationDate()));
            }
            
            if (fontSize != settings.getFontSize()) { 
                fontSize = settings.getFontSize();
                tile.getCenter().setStyle("-fx-font-size: " + 
                        (DisplayService.create()
                                .map(d -> d.isTablet() ? 1.4 : 1.0)
                                .orElse(1.0) * fontSize) + "pt;");
            }
        } else {
            tile.textProperty().clear();
        }
    }
    
    private String getSecondLine(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        int eol = text.indexOf("\n", 0);
        return eol < 1 ? text : text.substring(0, eol) + "...";
    }
    
}
