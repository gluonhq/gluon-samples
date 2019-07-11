/*
 * Copyright (c) 2019, Gluon
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
package com.gluonhq.functionmapper.views;

import com.gluonhq.functionmapper.model.StackEntry;
import com.gluonhq.functionmapper.model.StackOwner;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ResourceBundle;

public class DetailController extends AbstractController {

    @FXML
    private BorderPane detail;

    @FXML
    private VBox question;

    @FXML
    private ListView<StackEntry> listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listView.setPlaceholder(new Label("No answers yet"));
        listView.setCellFactory(p -> new ListCell<>() {

            private final ListTile tile;
            private final ImageView imageView;

            {
                tile = new ListTile();
                imageView = new ImageView();
                tile.setPrimaryGraphic(imageView);
            }

            @Override
            public void updateItem(StackEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    imageView.setImage(Util.getImage(item.getOwner().getProfile_image()));
                    tile.textProperty().setAll(item.getOwner().getDisplay_name(),
                            "Reputation: " + item.getOwner().getReputation(),
                            "Answered: " + Util.FORMATTER.format(LocalDateTime.ofEpochSecond(item.getCreation_date(), 0, ZoneOffset.UTC)));
                    setGraphic(tile);
                } else {
                    setGraphic(null);
                }
            }

        });
    }

    @Override
    public Parent getRoot() {
        return detail;
    }

    public void backToMain(ActionEvent event) {
        AbstractController mainController = getApp().getController("main");
        getApp().setRoot(mainController.getRoot());
    }

    public void setStackEntry(StackEntry stackEntry) {
        StackOwner stackOwner = stackEntry.getOwner();
        if (stackOwner != null) {
            createListTile(stackEntry, stackOwner);
        }
        search(String.valueOf(stackEntry.getQuestion_id()));
      }

    private void createListTile(StackEntry stackEntry, StackOwner stackOwner) {
        ImageView imageView = new ImageView(Util.getImage(stackOwner.getProfile_image()));
        imageView.setPreserveRatio(true);
        ListTile listTile = new ListTile();
        listTile.setPrimaryGraphic(imageView);
        listTile.textProperty().setAll(stackEntry.getTitle(),
                stackOwner.getDisplay_name(),
                "Created: " + Util.FORMATTER.format(LocalDateTime.ofEpochSecond(stackEntry.getCreation_date(), 0, ZoneOffset.UTC)) +
                    " - Answers: " + stackEntry.getAnswer_count());
        Button open = new Button("Open");
        open.setOnAction(e -> getApp().getHostServices().showDocument(stackEntry.getLink()));
        listTile.setSecondaryGraphic(open);
        if (question.getChildren().size() == 2) {
            question.getChildren().set(1, listTile);
        } else {
            question.getChildren().add(listTile);
        }
    }

    private void search(String questionId) {
        listView.setItems(FXCollections.emptyObservableList());
        var answersStackOverflow = getApp().service().answersStackOverflow(questionId);
        answersStackOverflow.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                listView.setItems(FXCollections.observableArrayList(answersStackOverflow.get().getItems()));
            }
        });
    }
}
