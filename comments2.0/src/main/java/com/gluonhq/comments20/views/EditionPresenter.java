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

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.DisplayService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.comments20.Comments20;
import com.gluonhq.comments20.cloud.Service;
import com.gluonhq.comments20.model.Comment;
import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.inject.Inject;

public class EditionPresenter extends GluonPresenter<Comments20> {

    @Inject 
    private Service service;
    
    @FXML
    private View edition;

    @FXML
    private Avatar avatar;
    
    @FXML
    private TextField authorText;

    @FXML
    private TextArea commentsText;

    @FXML
    private Button submit;

    private boolean editMode;

    public void initialize() {
        edition.setShowTransitionFactory(BounceInRightTransition::new);
        PseudoClass pseudoClassDisable = PseudoClass.getPseudoClass("disabled");
                
        edition.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                
                submit.disableProperty().unbind();
                    
                Comment activeComment = service.activeCommentProperty().get();
                if (activeComment != null) {
                    // disable avatar in case comment is not editable
                    // content is enabled if user is its author
                    commentsText.setDisable(!activeComment.getNetworkId().equals(service.getUser().getNetworkId()));
                    avatar.pseudoClassStateChanged(pseudoClassDisable, commentsText.isDisable());
                    avatar.setImage(Service.getUserImage(activeComment.getImageUrl()));
                
                    authorText.setText(activeComment.getAuthor());
                    commentsText.setText(activeComment.getContent());
                    
                    submit.setText("Apply");
                    submit.disableProperty().bind(Bindings.createBooleanBinding(()->{
                        return authorText.textProperty()
                                .isEqualTo(activeComment.getAuthor())
                                .and(commentsText.textProperty()
                                        .isEqualTo(activeComment.getContent())).get();
                        }, authorText.textProperty(),commentsText.textProperty()));
                    editMode = true;
                } else {
                    commentsText.setDisable(false);
                    avatar.pseudoClassStateChanged(pseudoClassDisable, false);
                    avatar.setImage(Service.getUserImage(service.getUser().getPicture()));
                    authorText.setText(service.getUser().getName());
                    
                    submit.setText("Submit");
                    submit.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                            return authorText.textProperty()
                                    .isEmpty()
                                    .or(commentsText.textProperty()
                                            .isEmpty()).get();
                        }, authorText.textProperty(), commentsText.textProperty()));
                    editMode = false;
                }
                
                authorText.setDisable(!authorText.getText().isEmpty());
                
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().getDrawer().open()));
                appBar.setTitleText("Edition");
            } else {
                authorText.clear();
                commentsText.clear();
            }
        });
        
        Services.get(DisplayService.class)
                .ifPresent(d -> {
                    if (d.isTablet()) {
                        avatar.getStyleClass().add("tablet");
                    }
                });
        avatar.setImage(Service.getUserImage(service.getUser().getPicture()));
    }
    
    @FXML
    void onCancel(ActionEvent event) {
        authorText.clear();
        commentsText.clear();
        service.activeCommentProperty().set(null);
        getApp().goHome();
    }

    @FXML
    void onSubmit(ActionEvent event) {
        Comment comment = editMode ? 
                service.activeCommentProperty().get() : 
                new Comment(authorText.getText(), commentsText.getText(), 
                            service.getUser().getPicture(), service.getUser().getNetworkId());
        comment.setContent(commentsText.getText());

        if (!editMode) {
            service.addComment(comment);
        }
        
        onCancel(event);
    }

}
