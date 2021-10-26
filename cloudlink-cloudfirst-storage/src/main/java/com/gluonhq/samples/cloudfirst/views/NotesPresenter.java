/*
 * Copyright (c) 2017, 2021, Gluon
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
package com.gluonhq.samples.cloudfirst.views;

import com.gluonhq.charm.glisten.animation.BounceInLeftTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.cloudfirst.model.Model;
import com.gluonhq.samples.cloudfirst.model.Note;
import com.gluonhq.samples.cloudfirst.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;

public class NotesPresenter {

    @Inject private Service service;
    
    @FXML private View notes;

    @Inject private Model model;
    
    @FXML private CharmListView<Note, LocalDate> lstNotes;
    
    @FXML private ResourceBundle resources;
    
    public void initialize() {
        notes.setShowTransitionFactory(BounceInLeftTransition::new);
        notes.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("appbar.notes"));
            }
        });
        
        lstNotes.setCellFactory(p -> new NoteCell(service, this::edit, this::remove));
        lstNotes.setHeadersFunction(t -> t.getCreationDate().toLocalDate());
        lstNotes.setHeaderCellFactory(p -> new HeaderCell());
        lstNotes.setComparator(Comparator.comparing(Note::getCreationDate));
        lstNotes.setHeaderComparator(LocalDate::compareTo);
        lstNotes.setPlaceholder(new Label(resources.getString("label.no.notes")));
        
        final FloatingActionButton floatingActionButton = new FloatingActionButton();
        floatingActionButton.setOnAction(e -> edit(null));
        floatingActionButton.showOn(notes);
        
        lstNotes.setItems(service.getNotes());
    }
    
    private void edit(Note note) {
        model.activeNote().set(note);
        AppViewManager.EDITION_VIEW.switchView();
    }
    
    private void remove(Note note) {
        service.removeNote(note);
    }
    
}
