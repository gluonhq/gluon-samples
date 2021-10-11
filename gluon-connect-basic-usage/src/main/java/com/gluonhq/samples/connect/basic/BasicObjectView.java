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

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.converter.JsonInputConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.InputStreamObjectDataReader;
import com.gluonhq.connect.provider.ObjectDataReader;
import com.gluonhq.connect.source.BasicInputDataSource;
import com.gluonhq.connect.source.InputDataSource;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class BasicObjectView extends View {

    public BasicObjectView() {

        Label lbName = new Label();
        CheckBox cbSubscribed = new CheckBox("Subscribed?");

        GridPane gridPane = new GridPane();
        gridPane.addRow(0, new Label("Name:"), lbName);
        gridPane.addRow(1, cbSubscribed);

        setCenter(gridPane);

        // create a DataSource that loads data from a classpath resource
        InputDataSource dataSource = new BasicInputDataSource(Main.class.getResourceAsStream("/user.json"));

        // create a Converter that converts a json object into a java object
        InputStreamInputConverter<User> converter = new JsonInputConverter<>(User.class);

        // create an ObjectDataReader that will read the data from the DataSource and converts
        // it from json into an object
        ObjectDataReader<User> objectDataReader = new InputStreamObjectDataReader<>(dataSource, converter);

        // retrieve an object from the DataProvider
        GluonObservableObject<User> user = DataProvider.retrieveObject(objectDataReader);

        // when the object is initialized, bind its properties to the JavaFX UI controls
        user.initializedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                lbName.textProperty().bind(user.get().nameProperty());
                cbSubscribed.selectedProperty().bindBidirectional(user.get().subscribedProperty());
            }
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> getAppManager().getDrawer().open()));
        appBar.setTitleText("Object Viewer");
    }

}
