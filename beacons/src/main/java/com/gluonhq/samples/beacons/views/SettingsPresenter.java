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

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.SettingsPane;
import com.gluonhq.charm.glisten.control.settings.DefaultOption;
import com.gluonhq.charm.glisten.control.settings.Option;
import com.gluonhq.charm.glisten.control.settings.OptionBase;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.beacons.settings.Settings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;

import javax.inject.Inject;

public class SettingsPresenter {

    @FXML
    private View settings;

    @Inject
    private Settings config;
    
    @FXML 
    private SettingsPane settingsPane;
    
    public void initialize() {
        settings.setShowTransitionFactory(BounceInRightTransition::new);
        
        settingsPane.setSearchBoxVisible(false);
        
        settings.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.CHEVRON_LEFT.button(e -> AppManager.getInstance().switchToPreviousView()));
                appBar.setTitleText("Settings");
                appBar.getActionItems().add(MaterialDesignIcon.SYNC.button(e -> {
                    config.setUuid(Settings.UUID);
                    config.setMajor(Settings.MAJOR);
                    config.setMinor(Settings.MINOR);
                    config.setId(Settings.ID);
                }));
                settingsPane.setTitleFilter("1");
                settingsPane.getOptions().clear();
            }
        });
    }

    void setupScanBeacon() {
        final Option<StringProperty> uuidOption = new DefaultOption<>(MaterialDesignIcon.BLUETOOTH_SEARCHING.graphic(),
                "Set the UUID", "Set the UUID to be scanned", "Scan Beacon Settings", config.uuidProperty(), true);

        ((OptionBase<StringProperty>) uuidOption).setLayout(Orientation.VERTICAL);

        settingsPane.getOptions().add(uuidOption);
        settingsPane.setTitleFilter("");
    }

    void setupBroadcastBeacon() {
        final OptionBase<StringProperty> uuidOption = new DefaultOption<>(MaterialDesignIcon.BLUETOOTH_SEARCHING.graphic(),
                "Set the UUID", "Set the UUID to be scanned", "Beacon Settings", config.uuidProperty(), true);

        uuidOption.setLayout(Orientation.VERTICAL);

        final OptionBase<StringProperty> majorOption = new DefaultOption<>(MaterialDesignIcon.DEVELOPER_MODE.graphic(),
                "Major value", "Set the major value", "Beacon Settings", config.majorProperty(), true);

        final OptionBase<StringProperty> minorOption = new DefaultOption<>(MaterialDesignIcon.DEVELOPER_BOARD.graphic(),
                "Minor value", "Set the major value", "Beacon Settings", config.minorProperty(), true);

        final OptionBase<StringProperty> idOption = new DefaultOption<>(MaterialDesignIcon.PERM_IDENTITY.graphic(),
                "Identifier", "Set the identifier", "Beacon Settings", config.idProperty(), true);

        settingsPane.getOptions().addAll(uuidOption, majorOption, minorOption, idOption);
        settingsPane.setTitleFilter("");
    }
}

