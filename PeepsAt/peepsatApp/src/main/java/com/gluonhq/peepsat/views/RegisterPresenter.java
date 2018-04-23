/*
 * Copyright (c) 2018, Gluon
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
package com.gluonhq.peepsat.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.PositionService;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.peepsat.Model;
import com.gluonhq.peepsat.PeepsAt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ResourceBundle;


public class RegisterPresenter extends GluonPresenter<PeepsAt> {
    
    @FXML
    View register;
    
    @FXML
    TextField username;

    @FXML
    private ResourceBundle resources;
    private PositionService positionService;

    public void initialize() {
        
        register.setOnShown(event -> {
            register.requestFocus();
            SettingsService settingsService = Services.get(SettingsService.class).get();
            final String myname = settingsService.retrieve("myname");
            if (myname != null) {
                Model.username = myname;
                AppViewManager.MAP_VIEW.switchView();
            }
        });
        
        register.setOnShowing(event -> {
            AppBar appBar = getApp().getAppBar();
            appBar.setTitleText(resources.getString("title"));
        });
    }
    
    @FXML
    public void signUp(ActionEvent event) {
        SettingsService settingsService = Services.get(SettingsService.class).get();
        settingsService.store("myname", username.getText());
        Model.username = username.getText();
        AppViewManager.MAP_VIEW.switchView();
    }

}
