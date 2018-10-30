/*
 * Copyright (c) 2016, 2018 Gluon
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

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.LifecycleEvent;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.client.user.User;
import com.gluonhq.comments20.Comments20;
import com.gluonhq.comments20.cloud.Service;
import com.gluonhq.comments20.model.Comment;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.ScrollEvent;

import javax.inject.Inject;
import java.util.Optional;

import static com.gluonhq.comments20.views.AppViewManager.EDITION_VIEW;

public class CommentsPresenter extends GluonPresenter<Comments20> {

    @Inject 
    private Service service;
    
    @FXML
    private View comments;

    @FXML
    private ListView<Comment> commentsList;
    
    private final BooleanProperty sliding = new SimpleBooleanProperty();
    
    private static final String SIGN_IN_MESSAGE = "Please, sign in first\n"
                + "to gain online access\n"
                + "to Comments v2.0";
    
    private static final String NO_COMMENTS_MESSAGE = "There are no comments\n"
            + "Click on the Action button\n"
            + "to create one";
    
    public void initialize() {
        comments.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().getDrawer().open()));
                appBar.setTitleText("Comments");
            }
        });
        
        FloatingActionButton floatingActionButton = new FloatingActionButton();
        floatingActionButton.textProperty().bind(new When(userProperty().isNotNull())
            .then(MaterialDesignIcon.ADD.text)
            .otherwise(MaterialDesignIcon.CLOUD_DOWNLOAD.text));
        floatingActionButton.setOnAction(e -> {
            if (service.getUser() == null) { 
                service.retrieveComments();
            } else {
                EDITION_VIEW.switchView();
            }
        });
        
        floatingActionButton.showOn(comments);
        
        commentsList.setCellFactory(cell -> { 
            final CommentListCell commentListCell = new CommentListCell(
                    service,
                    // left button: delete comment, only author's comment can delete it
                    c -> {
                        if (service.getUser().getNetworkId().equals(c.getNetworkId())) {
                           showDialog(c);
                        }
                    },
                    // right button: edit comment, everybody can view it, only author can edit it
                    c -> {
                       service.activeCommentProperty().set(c);
                       EDITION_VIEW.switchView();
                    });
            
            // notify view that cell is sliding
            commentListCell.slidingProperty().addListener((obs, ov, nv) -> sliding.set(nv));
        
            return commentListCell; 
        });
        
        final Label label = new Label(SIGN_IN_MESSAGE);
        label.textProperty().bind(new When(userProperty().isNotNull())
                .then(NO_COMMENTS_MESSAGE)
                .otherwise(SIGN_IN_MESSAGE));
        commentsList.setPlaceholder(label);
        commentsList.disableProperty().bind(service.userProperty().isNull());
        commentsList.setItems(service.commentsProperty());
        
        comments.addEventHandler(LifecycleEvent.SHOWN, new EventHandler<LifecycleEvent>() {
            @Override
            public void handle(LifecycleEvent event) {
                comments.removeEventHandler(LifecycleEvent.SHOWN, this);
                service.retrieveComments();
            }
        });
        
        // block scrolling when sliding
        comments.addEventFilter(ScrollEvent.ANY, e -> {
            if (sliding.get() && e.getDeltaY() != 0) {
                e.consume();
            }
        });
    }
    
    /**
     * Create a Dialog for getting deletion confirmation
     * @param item Item to be deleted
     */
    private void showDialog(Comment item) {
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
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
        alert.getButtons().setAll(yes,no);
        
        Optional result = alert.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.YES)){
            /*
            With confirmation, delete the item from the ListView. This will be
            propagated to the Cloud, and from here to the rest of the clients
            */
            commentsList.getItems().remove(item);
        }    
    }
    
    private ObjectProperty<User> userProperty() {
        return service.userProperty();
    }
}
