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
package com.gluonhq.samples.comments.views;

import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.comments.model.Comment;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Custom format for ListCells on the ListView control
 * It uses ListTile control
 */
public class CommentListCell extends ListCell<Comment> {

    private final ListTile tile = new ListTile();
    private Comment comment;
    
    {
        final Button button = MaterialDesignIcon.DELETE.button(e -> showDialog(comment));
        // Second graphic area, on the right
        tile.setSecondaryGraphic(new VBox(button));
    }
    
    /**
     * Add a ListTile control to not empty cells
     * @param item the comment on the cell
     * @param empty empty cell
     */
    @Override
    protected void updateItem(Comment item, boolean empty) {
        super.updateItem(item, empty);
        comment = item;
        if (!empty && item != null) {
            
            // Text
            tile.textProperty().setAll(item.getAuthor(), item.getContent());
        
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }
    
    /**
     * Create a Dialog for getting deletion confirmation
     * @param item Item to be deleted
     */
    private void showDialog(Comment item) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitleText("Confirm deletion");
        alert.setContentText("This comment will be deleted permanently.\nDo you want to continue?");
        
        Button yes = new Button("Yes, delete permanently");
        yes.setOnAction(e -> {
            alert.setResult(ButtonType.YES); 
            alert.hide();
        });
        yes.setDefaultButton(true);
        
        Button no = new Button("No");
        no.setCancelButton(true);
        no.setOnAction(e -> {
            alert.setResult(ButtonType.NO); 
            alert.hide();
        });
        alert.getButtons().setAll(yes, no);
        
        Optional result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(ButtonType.YES)) {
            /*
            With confirmation, delete the item from the ListView. This will be
            propagated to the Cloud, and from there to the rest of the clients
            */
            listViewProperty().get().getItems().remove(item);
        }
    }

}
