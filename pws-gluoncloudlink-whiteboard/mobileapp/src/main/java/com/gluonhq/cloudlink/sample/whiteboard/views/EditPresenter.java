/*
 * Copyright (c) 2016, 2017 Gluon
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

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.sample.whiteboard.Whiteboard;
import com.gluonhq.cloudlink.sample.whiteboard.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class EditPresenter extends GluonPresenter<Whiteboard> {

    @Inject private Model model;

    @FXML private View edit;

    @FXML private Button submit;
    @FXML private Button cancel;
    @FXML private TextField title;

    public void initialize() {
        edit.setShowTransitionFactory(BounceInRightTransition::new);

        edit.showingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                Item activeItem = model.activeItem().get();
                if (activeItem != null) {
                    submit.setText("APPLY");
                    title.setText(activeItem.getTitle());
                } else {
                    submit.setText("SUBMIT");
                }

                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(event -> getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText(model.activeItem().get() == null ? "Add Item" : "Edit Item");
            }
        });

        submit.disableProperty().bind(title.textProperty().isEmpty());
        submit.setOnAction(event -> {
            Item item = model.activeItem().get() == null ? new Item() : model.activeItem().get();
            item.setTitle(title.getText());
            if (model.activeItem().get() == null) {
                model.getItems().add(item);
            }
            close();
        });

        cancel.setOnAction(event -> close());
    }

    private void close() {
        title.clear();
        model.activeItem().set(null);

        getApp().goHome();
    }
}
