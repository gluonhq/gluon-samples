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

import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.functionmapper.model.StackEntry;
import com.gluonhq.functionmapper.model.StackResponse;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.ResourceBundle;

public class MainController extends AbstractController {

    private static final List<String> TAGS = List.of("gluon", "gluon-mobile", "javafxports", "javafx", "scenebuilder");

    @FXML
    private BorderPane main;

    @FXML
    private HBox top;

    @FXML
    private ComboBox<String> tagComboBox;
    
    @FXML
    private ListView<StackEntry> listView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tagComboBox.getItems().addAll(TAGS);
        tagComboBox.setEditable(true);

        listView.setPlaceholder(new Label("No items yet\nSelect a tag"));

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
                    tile.textProperty().setAll(item.getTitle(),
                            item.getOwner().getDisplay_name(),
                            "Created: " + Util.FORMATTER.format(LocalDateTime.ofEpochSecond(item.getCreation_date(), 0, ZoneOffset.UTC)) +
                                    " - Answers: " + item.getAnswer_count());
                    tile.setOnMouseClicked(e -> {
                        DetailController detailController =
                                (DetailController) getApp().getController("detail");
                        detailController.setStackEntry(item);
                        getApp().setRoot(detailController.getRoot());
                    });
                    setGraphic(tile);
                } else {
                    setGraphic(null);
                }
            }

        });
        
        tagComboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> search(nv));
    }

    @Override
    public Parent getRoot() {
        return main;
    }
    
    private void search(String tag) {
        reset();
        GluonObservableObject<StackResponse> searchStackOverflow = getApp().service().searchStackOverflow(StackResponse.class, tag);
        searchStackOverflow.setOnSucceeded(e -> {
            listView.setItems(FXCollections.observableArrayList(searchStackOverflow.get().getItems()));
            if (top.getChildren().size() == 3) {
                top.getChildren().remove(2);
            }
        });
        searchStackOverflow.setOnFailed(e -> {
            if (top.getChildren().size() == 3) {
                top.getChildren().set(2, new Label("Please try again"));
            }
        });
    }

    private void reset() {
        if (top.getChildren().size() == 2) {
            top.getChildren().add(new ProgressIndicator());
        } else if  (top.getChildren().get(2) instanceof Label) {
            top.getChildren().set(2, new ProgressIndicator());
        }
        listView.setItems(FXCollections.emptyObservableList());
    }
}
