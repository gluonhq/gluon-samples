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
import com.gluonhq.attach.ble.Configuration;
import com.gluonhq.attach.ble.ScanDetection;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.samples.beacons.settings.Settings;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.util.function.Consumer;

public class BeaconsPresenter {

    @Inject
    Settings settings;
	
    @FXML
    private View beacons;

    @FXML
    private CharmListView<ScanDetection, String> beaconsList;

    public void initialize() {
        Consumer<ScanDetection> callback = (ScanDetection t) -> {
            // update beacon view
            AppViewManager.BEACON_VIEW.getPresenter().ifPresent(p -> {
                BeaconPresenter presenter = (BeaconPresenter) p;
                ScanDetection currentBeacon = presenter.getCurrentBeacon();
                if (currentBeacon != null && currentBeacon.getUuid().equals(t.getUuid()) &&
                        currentBeacon.getMajor() == t.getMajor() &&
                        currentBeacon.getMinor() == t.getMinor()) {
                    presenter.setBeacon(t);
                }
            });
            // update list
            if (t.getUuid().equalsIgnoreCase(settings.getUuid()) &&
                    beaconsList.itemsProperty().stream().noneMatch(s ->
                            s.getMajor() == t.getMajor() && s.getMinor() == t.getMinor())) {
                javafx.application.Platform.runLater(() ->
                        beaconsList.itemsProperty().add(t));
            }
        };

        beaconsList.setPlaceholder(new Label("No beacons found"));
        beaconsList.setCellFactory(p -> new CharmListCell<>() {

            private ScanDetection scan;
            private final ListTile tile;

            {
                tile = new ListTile();
                tile.setPrimaryGraphic(MaterialDesignIcon.BLUETOOTH.graphic());
                tile.setSecondaryGraphic(MaterialDesignIcon.CHEVRON_RIGHT.graphic());
                tile.setOnMouseClicked(e ->
                        AppViewManager.BEACON_VIEW.switchView().ifPresent(presenter ->
                        ((BeaconPresenter) presenter).setBeacon(scan)));
            }

            @Override
            public void updateItem(ScanDetection item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    scan = item;
                    tile.setTextLine(0, "UUID: " + item.getUuid());
                    tile.setTextLine(1, "Major: " + item.getMajor());
                    tile.setTextLine(2, "Minor: " + item.getMinor());
                    setGraphic(tile);
                } else {
                    setGraphic(null);
                }
            }
        });

        ObservableList<Button> actions =
                BleService.create()
                        .map(bleService -> {
                            final Button buttonScan = MaterialDesignIcon.BLUETOOTH_SEARCHING.button();
                            final Button buttonStop = MaterialDesignIcon.STOP.button();
                            buttonScan.setOnAction(e -> {
                                bleService.stopScanning();
                                Configuration conf = new Configuration(settings.getUuid());
                                bleService.startScanning(conf, callback);
                                buttonStop.setDisable(false);
                            });
                            buttonStop.setOnAction(e -> {
                                bleService.stopScanning();
                                buttonStop.setDisable(true);
                                beaconsList.itemsProperty().clear();
                            });
                            buttonScan.disableProperty().bind(buttonStop.disableProperty().not());
                            buttonStop.setDisable(true);

                            return FXCollections.observableArrayList(
                                    MaterialDesignIcon.SETTINGS.button(e ->
                                            AppViewManager.SETTINGS_VIEW.switchView()
                                                    .ifPresent(p -> ((SettingsPresenter) p).setupScanBeacon())),
                                    buttonScan, buttonStop);
                        })
                        .orElseGet(() -> FXCollections.singletonObservableList(
                                MaterialDesignIcon.SETTINGS.button(e ->
                                        AppViewManager.SETTINGS_VIEW.switchView()
                                                .ifPresent(p -> ((SettingsPresenter) p).setupScanBeacon()))));

        beacons.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.CHEVRON_LEFT.button(e ->
                        AppManager.getInstance().goHome()));
                appBar.setTitleText("Scan Beacons");
                appBar.getActionItems().setAll(actions);
            }
        });
    }
    
}
