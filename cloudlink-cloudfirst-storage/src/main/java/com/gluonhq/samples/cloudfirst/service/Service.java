/**
 * Copyright (c) 2017, 2020 Gluon
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
package com.gluonhq.samples.cloudfirst.service;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.samples.cloudfirst.model.Note;
import javax.annotation.PostConstruct;

public class Service {
    
    private static final String NOTES = "notes-cloudfirst";

    private GluonObservableList<Note> notes;
    
    private DataClient dataClient;
    
    @PostConstruct
    public void postConstruct() {
        
        dataClient = DataClientBuilder.create()
                .operationMode(OperationMode.CLOUD_FIRST)
                .build();
        
        notes = retrieveNotes();
    }
    
    private GluonObservableList<Note> retrieveNotes() {
        // Retrieve notes from cloud or local storage
        return DataProvider.retrieveList(
                dataClient.createListDataReader(NOTES, Note.class,
                SyncFlag.LIST_WRITE_THROUGH,
                SyncFlag.OBJECT_WRITE_THROUGH));
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
