/**
 * Copyright (c) 2016, 2017 Gluon
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
package com.gluonhq.pushnotes.views;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.afterburner.GluonView;
import com.gluonhq.charm.glisten.animation.BounceInLeftTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.pushnotes.PushNotes;
import com.gluonhq.pushnotes.model.Model;
import com.gluonhq.pushnotes.model.Note;
import com.gluonhq.pushnotes.model.Settings;
import com.gluonhq.pushnotes.service.Service;
import java.time.LocalDate;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import javax.inject.Inject;

public class NotesPresenter extends GluonPresenter<PushNotes> {

    @Inject private Service service;
    
    @FXML private View notes;

    @Inject private Model model;
    
    @FXML private CharmListView<Note, LocalDate> lstNotes;
    
    private FilteredList<Note> filteredList;
    
    public void initialize() {
        notes.setShowTransitionFactory(BounceInLeftTransition::new);
        notes.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText("Notes");
                appBar.getActionItems().add(MaterialDesignIcon.FILTER_LIST.button(e -> {
                    getApp().showLayer(PushNotes.POPUP_FILTER_NOTES);
                }));
            }
        });
        
        lstNotes.setCellFactory(p -> new NoteCell(service, this::edit, this::remove));
        lstNotes.setHeadersFunction(t -> t.getCreationDate().toLocalDate());
        lstNotes.setHeaderCellFactory(p -> new HeaderCell());
        lstNotes.setPlaceholder(new Label("There are no notes"));
        
        service.notesProperty().addListener((ListChangeListener.Change<? extends Note> c) -> {
            filteredList = new FilteredList(c.getList());
            lstNotes.setItems(filteredList);
            lstNotes.setComparator((n1, n2) -> n1.getCreationDate().compareTo(n2.getCreationDate()));
        });
        
        final FloatingActionButton floatingActionButton = new FloatingActionButton();
        floatingActionButton.setOnAction(e -> edit(null));
        notes.getLayers().add(floatingActionButton.getLayer());
        
        getApp().addLayerFactory(PushNotes.POPUP_FILTER_NOTES, () -> { 
            GluonView view = new GluonView(FilterPresenter.class);
            SidePopupView sidePopupView = new SidePopupView(view.getView(), Side.TOP, true);
            sidePopupView.showingProperty().addListener((obs, ov, nv) -> {
                if (ov && !nv) {
                    filteredList.setPredicate(((FilterPresenter) view.getPresenter()).getPredicate());
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
            lstNotes.setHeaderComparator((h1, h2) -> h1.compareTo(h2));
        } else {
            lstNotes.setHeaderComparator((h1, h2) -> h2.compareTo(h1));
        }
        
        switch (settings.getSorting()) {
            case DATE:  
                if (settings.isAscending()) {
                    lstNotes.setComparator((n1, n2) -> n1.getCreationDate().compareTo(n2.getCreationDate()));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getCreationDate().compareTo(n1.getCreationDate()));
                }
                break;
            case TITLE: 
                if (settings.isAscending()) {
                    lstNotes.setComparator((n1, n2) -> n1.getTitle().compareTo(n2.getTitle()));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getTitle().compareTo(n1.getTitle()));
                }
                break;
            case CONTENT: 
                if (settings.isAscending()) {
                    lstNotes.setComparator((n1, n2) -> n1.getText().compareTo(n2.getText()));
                } else {
                    lstNotes.setComparator((n1, n2) -> n2.getText().compareTo(n1.getText()));
                }
                break;
        }
    }
    
}