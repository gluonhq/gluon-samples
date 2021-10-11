/*
 * Copyright (c) 2021, Gluon
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
package com.gluonhq.samples.alarm.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.alarm.model.Event;
import com.gluonhq.samples.alarm.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;

public class EventsPresenter {

    @FXML private View result;

    @FXML private ListView<Event> listView;
    @FXML private ResourceBundle resources;

    @Inject
    private Service service;

    public void initialize() {
        result.setShowTransitionFactory(BounceInRightTransition::new);

        result.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("appbar.title"));
                appBar.getActionItems().add(MaterialDesignIcon.ALARM_ADD.button(e ->
                        AppViewManager.ALARM_VIEW.switchView()));
            }
        });

        listView.setPlaceholder(new Label(resources.getString("label.empty.text")));
        listView.setCellFactory(p -> new EventCell(service));
        service.notesProperty().addListener((ListChangeListener.Change<? extends Event> c) -> {
            ObservableList<Event> events = FXCollections.observableArrayList(new ArrayList<Event>(c.getList()));
            SortedList<Event> sortedList = new SortedList<>(events, Comparator.comparing(Event::getCreationDate));
            listView.setItems(sortedList);
        });

        service.retrieveEvents(false);
    }

    public void selectEvent(String eventId) {
        listView.getItems().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .ifPresent(e -> {
                    listView.scrollTo(e);
                    listView.getSelectionModel().select(e);
                });
    }

}
