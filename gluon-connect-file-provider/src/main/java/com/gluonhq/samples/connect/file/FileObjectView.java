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
package com.gluonhq.samples.connect.file;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.converter.JsonInputConverter;
import com.gluonhq.connect.converter.JsonOutputConverter;
import com.gluonhq.connect.converter.OutputStreamOutputConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.FileClient;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.IOException;

import static com.gluonhq.samples.connect.file.Main.ROOT_DIR;

public class FileObjectView extends View {

    public FileObjectView() throws IOException {

        Label lbName = new Label();
        CheckBox cbSubscribed = new CheckBox("Subscribed?");

        GridPane gridPane = new GridPane();
        gridPane.addRow(0, new Label("Name:"), lbName);
        gridPane.addRow(1, cbSubscribed);

        setCenter(gridPane);

        // create a FileClient to the specified file
        FileClient fileClient = FileClient.create(new File(ROOT_DIR, "user.json"));

        // create a JSON converter that converts a JSON object into a user object
        InputStreamInputConverter<User> converter = new JsonInputConverter<>(User.class);

        // retrieve an object from an ObjectDataReader created from the FileClient
        GluonObservableObject<User> user = DataProvider.retrieveObject(fileClient.createObjectDataReader(converter));

        // when the object is initialized, bind its properties to the JavaFX UI controls
        user.initializedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                lbName.textProperty().bind(user.get().nameProperty());
                cbSubscribed.selectedProperty().bindBidirectional(user.get().subscribedProperty());
            }
        });

        // write user to file when selected property of the subscribed checkbox is changed
        cbSubscribed.selectedProperty().addListener((obs, ov, nv) -> {
            user.get().setSubscribed(nv);

            // create a JSON converter that converts the user object into a JSON object
            OutputStreamOutputConverter<User> outputConverter = new JsonOutputConverter<>(User.class);

            // store an object with an ObjectDataWriter created from the FileClient
            DataProvider.storeObject(user.get(), fileClient.createObjectDataWriter(outputConverter));
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> getAppManager().getDrawer().open()));
        appBar.setTitleText("File Object Viewer");
    }

}
