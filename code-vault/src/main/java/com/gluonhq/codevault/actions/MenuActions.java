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
package com.gluonhq.codevault.actions;

import com.gluonhq.codevault.view.RepoManagerView;
import com.gluonhq.particle.annotation.ParticleActions;
import com.gluonhq.particle.application.ParticleApplication;
import com.gluonhq.particle.view.View;
import com.gluonhq.particle.view.ViewManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.inject.Inject;

import javafx.stage.DirectoryChooser;
import org.controlsfx.control.action.ActionProxy;

import java.util.Optional;

@ParticleActions
public class MenuActions {

    @Inject
    ParticleApplication app;

    @Inject private ViewManager viewManager;

    @ActionProxy(text="Exit",
                 graphic="font>github-octicons|SIGN_OUT",
                 accelerator="alt+F4")
    private void exit() {
        app.exit();
    }
    
    @ActionProxy(text="About",
            graphic="font>github-octicons|MARK_GITHUB",
            accelerator="ctrl+A")
    private void about() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("CodeVault");
        alert.setHeaderText("About CodeVault");
        alert.setGraphic(new ImageView(new Image(MenuActions.class.getResource("/icon.png").toExternalForm(), 48, 48, true, true)));
        alert.setContentText("This is a Gluon Desktop Application that creates a simple Git Repository");
        alert.showAndWait();
    }

    @ActionProxy(
            text="Open Repository",
            graphic="font>FontAwesome|FOLDER_OPEN",
            accelerator="ctrl+O")
    private void openRepo() {
        View currentView = viewManager.getCurrentView();

        if (currentView instanceof RepoManagerView) {
            RepoManagerView view = (RepoManagerView) currentView;
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Open Git Repository");
            Optional.ofNullable(dirChooser.showDialog(app.getPrimaryStage())).ifPresent(view::openRepo);
        }
    }
    
}