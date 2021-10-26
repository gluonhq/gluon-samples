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
package com.gluonhq.samples.beacons.views;

import com.gluonhq.attach.ble.BleService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.beacons.settings.Settings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.util.UUID;

public class BroadcastPresenter {

    @FXML
    private View broadcastView;

    @FXML
    private Label labelUUID;

    @FXML
    private Label labelMajor;

    @FXML
    private Label labelMinor;

    @FXML
    private Label labelId;

    @Inject
    Settings settings;

    private final BooleanProperty scanning = new SimpleBooleanProperty();

    public void initialize() {
        labelUUID.textProperty().bind(settings.uuidProperty());
        labelMajor.textProperty().bind(settings.majorProperty());
        labelMinor.textProperty().bind(settings.minorProperty());
        labelId.textProperty().bind(settings.idProperty());

        Button playButton = MaterialDesignIcon.PLAY_ARROW.button(e -> {
            BleService.create().ifPresent(ble -> ble.startBroadcasting(UUID.fromString(labelUUID.getText()),
                    Integer.parseInt(labelMajor.getText()),
                    Integer.parseInt(labelMinor.getText()),
                    labelId.getText()));
            scanning.set(true);
        });
        playButton.disableProperty().bind(scanning);
        Button stopButton = MaterialDesignIcon.STOP.button(e -> {
            scanning.set(false);
            BleService.create().ifPresent(BleService::stopBroadcasting);
        });
        stopButton.disableProperty().bind(scanning.not());
        Button settingsButton = MaterialDesignIcon.SETTINGS.button(e -> {
            if (scanning.get()) {
                stopButton.fire();
            }
            AppViewManager.SETTINGS_VIEW.switchView()
                    .ifPresent(p -> ((SettingsPresenter) p).setupBroadcastBeacon());
        });
        broadcastView.showingProperty().addListener((obs, oldValue, newValue) -> {
            AppBar appBar = AppManager.getInstance().getAppBar();
            appBar.setNavIcon(MaterialDesignIcon.CHEVRON_LEFT.button(e ->
                    AppManager.getInstance().goHome()));
            appBar.setTitleText("Broadcast Beacon");
            BleService.create()
                .map(ble -> appBar.getActionItems().setAll(settingsButton, playButton, stopButton))
                .orElseGet(() -> appBar.getActionItems().setAll(settingsButton));
        });
    }
    
}
