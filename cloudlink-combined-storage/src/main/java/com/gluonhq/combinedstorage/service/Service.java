/**
 * Copyright (c) 2017, Gluon
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
package com.gluonhq.combinedstorage.service;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.combinedstorage.model.Note;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javax.annotation.PostConstruct;

public class Service {
    
    private static final String NOTES = "notes-combined";
    
    /**
     * True uses cloud storage with preference over local storage
     * False disables cloud storage
     */
    private static final boolean CLOUD_STORAGE_ENABLED = true;

    private GluonObservableList<Note> notes;
    
    private DataClient localDataClient;
    private DataClient cloudDataClient;
    
    @PostConstruct
    public void postConstruct() {
        localDataClient = DataClientBuilder.create()
                .operationMode(OperationMode.LOCAL_ONLY)
                .build();
        
        cloudDataClient = DataClientBuilder.create()
                .operationMode(OperationMode.CLOUD_FIRST)
                .build();
        
        notes = retrieveNotes();
    }
    
    private GluonObservableList<Note> retrieveNotes() {
        // Read local notes first
        GluonObservableList<Note> internalLocalNotes = DataProvider.retrieveList(
                localDataClient.createListDataReader(NOTES, Note.class,
                            SyncFlag.LIST_WRITE_THROUGH,
                            SyncFlag.OBJECT_WRITE_THROUGH));

        internalLocalNotes.initializedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                if (CLOUD_STORAGE_ENABLED) { 
                    // Retrieve cloud notes 
                    GluonObservableList<Note> internalCloudNotes = DataProvider.retrieveList(
                            cloudDataClient.createListDataReader(NOTES, Note.class,
                            SyncFlag.LIST_WRITE_THROUGH,
                            SyncFlag.OBJECT_WRITE_THROUGH));

                    internalCloudNotes.initializedProperty().addListener((obs2, ov2, nv2) -> {
                        if (nv2) {
                            // add to local new notes from cloud
                            for (Note cloudNote : internalCloudNotes) {
                                if (!internalLocalNotes.contains(cloudNote)) {
                                    internalLocalNotes.add(cloudNote);
                                }
                            }
                    
                            // remove from local if it doesn't exist in the cloud
                            List<Note> toRemove = new ArrayList<>();
                            for (Note localNote : internalLocalNotes) {
                                if (!internalCloudNotes.contains(localNote)) {
                                    toRemove.add(localNote);
                                }
                            }
                            internalLocalNotes.removeAll(toRemove);
                    
                            // bind content between local and cloud lists
                            Bindings.bindContent(internalCloudNotes, internalLocalNotes);
                        }
                    });
                }
            }
        });
        return internalLocalNotes;
    }
    
    public Note addNote(Note note) {
        notes.add(note);
        return note;
    }

    public void removeNote(Note note) {
        notes.remove(note);
    }

    public GluonObservableList<Note> getNotes() {
        return notes;
    }
    
}
