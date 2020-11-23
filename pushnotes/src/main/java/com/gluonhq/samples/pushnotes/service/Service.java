/*
 * Copyright (c) 2016, 2020, Gluon
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
package com.gluonhq.samples.pushnotes.service;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.samples.pushnotes.model.Note;
import com.gluonhq.samples.pushnotes.model.Settings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javax.annotation.PostConstruct;

public class Service {

    private static final String NOTES = "notes-v4";

    private static final String NOTES_SETTINGS = "notes-settings-v4";

    private final ListProperty<Note> notes = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final ObjectProperty<Settings> settings = new SimpleObjectProperty<>(new Settings());

    private DataClient dataClient;

    @PostConstruct
    public void postConstruct() {
        dataClient = DataClientBuilder.create()
                .operationMode(OperationMode.CLOUD_FIRST)
                .build();
    }

    public void retrieveNotes() {
        GluonObservableList<Note> gluonNotes = DataProvider.<Note>retrieveList(
                dataClient.createListDataReader(NOTES, Note.class,
                        SyncFlag.LIST_WRITE_THROUGH, SyncFlag.LIST_READ_THROUGH,
                        SyncFlag.OBJECT_WRITE_THROUGH, SyncFlag.OBJECT_READ_THROUGH));

        gluonNotes.stateProperty().addListener((obs, ov, nv) -> {
            if (ConnectState.SUCCEEDED.equals(nv)) {
                notes.set(gluonNotes);

                retrieveSettings();
            }
        });
    }

    public Note addNote(Note note) {
        notes.get().add(note);
        return note;
    }

    public void removeNote(Note note) {
        notes.get().remove(note);
    }

    public ListProperty<Note> notesProperty() {
        return notes;
    }

    private void retrieveSettings() {
        GluonObservableObject<Settings> gluonSettings = DataProvider.<Settings>retrieveObject(
                dataClient.createObjectDataReader(NOTES_SETTINGS, Settings.class,
                        SyncFlag.OBJECT_WRITE_THROUGH, SyncFlag.OBJECT_READ_THROUGH));
        gluonSettings.stateProperty().addListener((obs, ov, nv) -> {
            if (ConnectState.SUCCEEDED.equals(nv) && gluonSettings.get() != null) {
                settings.set(gluonSettings.get());
            }
        });
    }

    public void storeSettings() {
        DataProvider.storeObject(settings.get(),
                dataClient.createObjectDataWriter(NOTES_SETTINGS, Settings.class));
    }

    public ObjectProperty<Settings> settingsProperty() {
        return settings;
    }

}
