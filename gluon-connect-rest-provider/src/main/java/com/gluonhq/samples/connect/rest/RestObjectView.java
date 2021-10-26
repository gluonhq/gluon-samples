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
package com.gluonhq.samples.connect.rest;

import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.browser.BrowserService;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URISyntaxException;

public class RestObjectView extends View {

    public RestObjectView() {

        Label lbQuestionId = new Label();
        Label lbTitle = new Label();
        Hyperlink hlLink = new Hyperlink();

        GridPane gridPane = new GridPane();
        gridPane.setVgap(5.0);
        gridPane.setHgap(5.0);
        gridPane.setPadding(new Insets(5.0));
        gridPane.addRow(0, new Label("Question ID:"), lbQuestionId);
        gridPane.addRow(1, new Label("Title:"), lbTitle);
        gridPane.addRow(2, new Label("Link:"), hlLink);
        gridPane.getColumnConstraints().add(new ColumnConstraints(75));

        lbQuestionId.setWrapText(true);
        lbTitle.setWrapText(true);
        hlLink.setWrapText(true);

        hlLink.setOnAction(e ->
            Services.get(BrowserService.class).ifPresent(service -> {
                try {
                    service.launchExternalBrowser(hlLink.getText());
                } catch (IOException | URISyntaxException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Failed to open URL");
                    alert.setContentText("Failed to open URL. Reason: " + ex.getMessage());
                    alert.showAndWait();
                }
            }));

        setCenter(gridPane);

        // create a RestClient to the specific URL
        RestClient restClient = RestClient.create()
                .method("GET")
                .host("https://api.stackexchange.com")
                .path("/2.2/questions/36243147")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow");

        // create a custom Converter that is able to parse the response into a single object
        InputStreamInputConverter<Question> converter = new SingleItemInputConverter<>(Question.class);

        // retrieve an object from the DataProvider
        GluonObservableObject<Question> question = DataProvider.retrieveObject(restClient.createObjectDataReader(converter));

        // when the object is initialized, bind its properties to the JavaFX UI controls
        question.initializedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                lbQuestionId.textProperty().bind(question.get().questionIdProperty().asString());
                lbTitle.textProperty().bind(question.get().titleProperty());
                hlLink.textProperty().bind(question.get().linkProperty());
            }
        });
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> getAppManager().getDrawer().open()));
        appBar.setTitleText("Rest Object Viewer");
    }

}
