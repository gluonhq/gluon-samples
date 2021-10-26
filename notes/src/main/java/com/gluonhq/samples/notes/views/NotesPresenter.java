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

import com.gluonhq.charm.glisten.afterburner.GluonView;
import com.gluonhq.charm.glisten.animation.BounceInLeftTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.notes.Notes;
import com.gluonhq.samples.notes.model.Model;
import com.gluonhq.samples.notes.model.Note;
import com.gluonhq.samples.notes.model.Settings;
import com.gluonhq.samples.notes.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

public class NotesPresenter {

    private static final PseudoClass PSEUDO_FILTER_ENABLED = PseudoClass.getPseudoClass("filter-enabled");

    @FXML private View notes;

    @Inject private Model model;
    
    @FXML private CharmListView<Note, LocalDate> lstNotes;

    @FXML private ResourceBundle resources;

    @Inject private Service service;

    private FilteredList<Note> filteredList;
    
    public void initialize() {
        Button filterButton = MaterialDesignIcon.FILTER_LIST.button(e ->
                AppManager.getInstance().showLayer(Notes.POPUP_FILTER_NOTES));
        filterButton.getStyleClass().add("filter-button");

        notes.setShowTransitionFactory(BounceInLeftTransition::new);
        notes.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("appbar.title"));
                appBar.getActionItems().add(filterButton);
            }
        });
        
        lstNotes.setCellFactory(p -> new NoteCell(service, this::edit, this::remove));
        lstNotes.setHeadersFunction(t -> t.getCreationDate().toLocalDate());
        lstNotes.setHeaderCellFactory(p -> new HeaderCell());
        lstNotes.setPlaceholder(new Label("There are no notes"));
        
        service.notesProperty().addListener((ListChangeListener.Change<? extends Note> c) -> {
            ObservableList<Note> notes = FXCollections.observableArrayList(new ArrayList<Note>(c.getList()));
            filteredList = new FilteredList<>(notes);
            lstNotes.setItems(filteredList);
            lstNotes.setComparator(Comparator.comparing(Note::getCreationDate));
        });
        
        final FloatingActionButton floatingActionButton = new FloatingActionButton();
        floatingActionButton.setOnAction(e -> edit(null));
        floatingActionButton.showOn(notes);
        
        AppManager.getInstance().addLayerFactory(Notes.POPUP_FILTER_NOTES, () -> {
            GluonView filterView = new GluonView(FilterPresenter.class);
            FilterPresenter filterPresenter = (FilterPresenter) filterView.getPresenter();
            
            SidePopupView sidePopupView = new SidePopupView(filterView.getView(), Side.TOP, true);
            sidePopupView.showingProperty().addListener((obs, ov, nv) -> {
                if (ov && !nv) {
                    filteredList.setPredicate(filterPresenter.getPredicate());
                    filterButton.pseudoClassStateChanged(PSEUDO_FILTER_ENABLED, filterPresenter.isFilterApplied());
                }
            });
            
            return sidePopupView; 
        });
        
        service.retrieveNotes();
        
        service.settingsProperty().addListener((obs, ov, nv) -> updateSettings());
        
        updateSettings();
    }
    
    private void edit(Note note) {
        model.activeNote().set(note);
        AppViewManager.EDITION_VIEW.switchView();
    }
    
    private void remove(Note note) {
        service.removeNote(note);
    }

    private void updateSettings() {
        Settings settings = service.settingsProperty().get();
        if (settings.isAscending()) {
            lstNotes.setHeaderComparator(Comparator.naturalOrder());
        } else {
            lstNotes.setHeaderComparator(Comparator.reverseOrder());
        }
        
        switch (settings.getSorting()) {
            case DATE:  
                if (settings.isAscending()) {
                    lstNotes.setComparator(Comparator.comparing(Note::getCreationDate));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getCreationDate().compareTo(n1.getCreationDate()));
                }
                break;
            case TITLE: 
                if (settings.isAscending()) {
                    lstNotes.setComparator(Comparator.comparing(Note::getTitle));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getTitle().compareTo(n1.getTitle()));
                }
                break;
            case CONTENT: 
                if (settings.isAscending()) {
                    lstNotes.setComparator(Comparator.comparing(Note::getText));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getText().compareTo(n1.getText()));
                }
                break;
        }
    }
}
