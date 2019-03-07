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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ListTile extends BorderPane {

    private ObservableList<String> textProperty = FXCollections.observableArrayList();
    private final VBox  textBox = new VBox();

    public ListTile() {
        getStyleClass().add("list-tile");
        textBox.getStyleClass().add("text-box");
        textProperty.addListener((ListChangeListener.Change<? extends String> c) -> updateText());
        setCenter(textBox);
    }

    public void setPrimaryGraphic(Node node) {
        BorderPane.setAlignment(node, Pos.CENTER);
        setLeft(node);
    }

    public void setSecondaryGraphic(Node node) {
        setRight(node);
    }

    public ObservableList<String> textProperty() {
        return textProperty;
    }

    private void updateText() {
        textBox.getChildren().clear();
        for (int i = 0; i <= 2; i++) {
            Label label = new Label(textProperty.get(i));
            label.setWrapText(true);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setAlignment(Pos.TOP_LEFT);
            if (i == 0) {
                label.getStyleClass().add("primary");
            }
            textBox.getChildren().add(label);
            VBox.setVgrow(label, Priority.ALWAYS);
        }
    }
}
