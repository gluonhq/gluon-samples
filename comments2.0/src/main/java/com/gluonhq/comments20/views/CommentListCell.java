/**
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
package com.gluonhq.comments20.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.DisplayService;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.comments20.cloud.Service;
import com.gluonhq.comments20.model.Comment;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;

/**
 * Custom list cell for adding sliding options
 * It uses a ListTile control, with two graphic zones
 * wrapped in a SlidingListTile control
 */
public class CommentListCell extends ListCell<Comment> {
    
    private final SlidingListTile slidingTile;
    private final ListTile tile;
    private final Avatar avatar;
    private final Service service;
    
    private Comment currentItem;
    private final ChangeListener<String> commentChangeListener;
    
    public CommentListCell(Service service, Consumer<Comment> consumerLeft, Consumer<Comment> consumerRight) {
        this.service = service;
        
        tile = new ListTile();
        avatar = new Avatar();
        Services.get(DisplayService.class)
                .ifPresent(d -> {
                    if (d.isTablet()) {
                        avatar.getStyleClass().add("tablet");
                    }
                });
        
        tile.setPrimaryGraphic(avatar);
        slidingTile = new SlidingListTile(tile, true, MaterialDesignIcon.DELETE.text, MaterialDesignIcon.EDIT.text);
        
        slidingTile.swipedLeftProperty().addListener((obs, ov, nv) -> {
            if (nv && consumerRight != null) {
                consumerRight.accept(currentItem);
            }
            slidingTile.resetTilePosition();
        });
        
        // delete mode, swipping from left to right
        slidingTile.swipedRightProperty().addListener((obs, ov, nv) -> {
            if (nv && consumerLeft != null) {
                consumerLeft.accept(currentItem);
            }
            slidingTile.resetTilePosition();
        });
        
        commentChangeListener = (obs, ov, nv) -> {
            // make sure text updates are run on the JavaFX thread, because the change can come from
            // an sse event from gluon cloud when another application updates a currentItem
            Platform.runLater(() -> {
                if (currentItem != null) {
                    tile.textProperty().setAll(currentItem.getAuthor());
                    tile.textProperty().addAll(getContent(currentItem));
                } else {
                    tile.textProperty().clear();
                }
            });
        };
    }
    
    /**
     * Add a ListTile control to not empty cells
     * @param item the comment on the cell
     * @param empty 
     */
    @Override
    protected void updateItem(Comment item, boolean empty) {
        super.updateItem(item, empty);
        updateCurrentItem(item);
        if (!empty && item != null) {
            avatar.setImage(Service.getUserImage(item.getImageUrl()));
            if (service.getUser() != null) {
                slidingTile.allowedProperty().set(service.getUser().getNetworkId().equals(item.getNetworkId()));
            }
            tile.textProperty().setAll(item.getAuthor());
            tile.textProperty().addAll(getContent(item));
            setGraphic(slidingTile);
            setPadding(Insets.EMPTY);
        } else {
            setGraphic(null);
        }
    }
    
    private void updateCurrentItem(Comment comment) {
        if (currentItem == null || !currentItem.equals(comment)) {
            if (currentItem != null) {
                currentItem.authorProperty().removeListener(commentChangeListener);
                currentItem.contentProperty().removeListener(commentChangeListener);
            }

            currentItem = comment;

            if (currentItem != null) {
                currentItem.authorProperty().addListener(commentChangeListener);
                currentItem.contentProperty().addListener(commentChangeListener);
            }
        }
    }
    
    private String[] getContent(Comment comment) {
        if (comment == null || comment.getContent() == null || comment.getContent().isEmpty()) {
            return new String[] {""};
        }
        
        final String[] lines = comment.getContent().split("\\n");
        if (lines.length > 2) {
            lines[1]=lines[1].concat(" ...");
        }
        return lines;
    }
    
    /**
     * Boolean property with sliding status of cell
     * @return 
     */
    public BooleanProperty slidingProperty() {
        return slidingTile.slidingProperty();
    }

}
