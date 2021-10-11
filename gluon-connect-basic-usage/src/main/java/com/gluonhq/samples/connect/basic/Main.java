/*
 * Copyright (c) 2016, 2021, Gluon
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
package com.gluonhq.samples.connect.basic;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class Main extends Application {

    public static final String LIST_VIEW = HOME_VIEW;
    public static final String OBJECT_VIEW = "ObjectView";
    private final AppManager appManager = AppManager.initialize(this::postInit);

    @Override
    public void init() {
        appManager.addViewFactory(LIST_VIEW, BasicListView::new);
        appManager.addViewFactory(OBJECT_VIEW, BasicObjectView::new);
        updateDrawer();
    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
    }

    private void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
    }

    private void updateDrawer() {
        NavigationDrawer navigationDrawer = appManager.getDrawer();
        NavigationDrawer.Header header = new NavigationDrawer.Header("Gluon Mobile", "Gluon Connect Sample",
                new Avatar(21, new Image(getClass().getResourceAsStream("/icon.png"))));
        navigationDrawer.setHeader(header);
        NavigationDrawer.Item listItem = new NavigationDrawer.Item("List Viewer", MaterialDesignIcon.VIEW_LIST.graphic());
        NavigationDrawer.Item objectItem = new NavigationDrawer.Item("Object Viewer", MaterialDesignIcon.INSERT_DRIVE_FILE.graphic());
        navigationDrawer.getItems().addAll(listItem, objectItem);
        navigationDrawer.selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem.equals(listItem)) {
                appManager.switchView(LIST_VIEW);
            } else if (newItem.equals(objectItem)) {
                appManager.switchView(OBJECT_VIEW);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
