/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gluonhq.demos.tictactoe;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class TicTacToe extends MobileApplication {

    private static String HOST =  null;
    private static final String HOST_KEY = "tic.tac.toe.url";

    @Override
    public void init() {
        addViewFactory(HOME_VIEW, () -> new MainView());
    }

    @Override
    public void postInit(Scene scene) {

        Swatch.BLUE_GREY.assignTo(scene);

        scene.getStylesheets().add(getClass().getResource("/tictactoe.css").toExternalForm());

        ((Stage) scene.getWindow()).getIcons().add(new Image(TicTacToe.class.getResourceAsStream("/icon.png")));

    }

    private static Optional<SettingsService> SETTINGS_SERVICE = Services.get(SettingsService.class);

    static final String getHost() {
        if ( HOST == null ) {
            SETTINGS_SERVICE.ifPresent(service -> HOST =  service.retrieve(HOST_KEY));
        }
        return HOST == null? "http://localhost:8090": HOST;
    }

    static final void setHost( String newHost ) {
        Services.get(SettingsService.class).ifPresent(service -> {
            service.store(HOST_KEY, newHost);
        });
        HOST = newHost;
    }
}
