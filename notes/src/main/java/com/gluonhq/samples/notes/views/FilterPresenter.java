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
package com.gluonhq.samples.notes.views;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.samples.notes.Notes;
import com.gluonhq.samples.notes.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.function.Predicate;

public class FilterPresenter {

    @FXML private TextField searchField;
    @FXML private ToolBar toolBar;

    public void initialize() {
        HBox.setHgrow(searchField, Priority.ALWAYS);

        if (Platform.isIOS() && ! toolBar.getStyleClass().contains("ios")) {
            toolBar.getStyleClass().add("ios");
        }
        boolean notch = DisplayService.create().map(DisplayService::hasNotch).orElse(false);
        if (notch && ! toolBar.getStyleClass().contains("notch")) {
            toolBar.getStyleClass().add("notch");
        }
    }
    
    @FXML
    private void search() {
        AppManager.getInstance().hideLayer(Notes.POPUP_FILTER_NOTES);
    }
    
    @FXML
    private void close() {
        searchField.clear();
        AppManager.getInstance().hideLayer(Notes.POPUP_FILTER_NOTES);
    }

    Predicate<? super Note> getPredicate() {
        return n -> n.getTitle().contains(searchField.getText()) || 
                    n.getText().contains(searchField.getText());
    }

    boolean isFilterApplied() {
        return searchField.getText() != null && ! searchField.getText().isEmpty();
    }
}
