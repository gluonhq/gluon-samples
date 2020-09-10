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
package com.gluonhq.samples.comments.cloud;

import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.samples.comments.model.Comment;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.provider.DataProvider;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javax.annotation.PostConstruct;

/**
 * Service to access the application on Gluon CloudLink, retrieve a list with comments
 * and send new comments to the list.
 */
public class Service {

    /*
    Every list stored under the same application on the Cloud has a unique id:
    */
    private static final String CLOUD_LIST_ID = "comments";

    private DataClient dataClient;

    /*
    An observable wrapper of the retrieved list, used to expose it and bind the 
    ListView items to the list.
    */
    private final ListProperty<Comment> commentsList = 
            new SimpleListProperty<>(FXCollections.<Comment>observableArrayList()); 
    
    @PostConstruct
    public void postConstruct() {
        dataClient = DataClientBuilder.create().build();
    }
    
    /**
     * Once there's a valid gluonClient, the contents of the list can be retrieved. This will return a 
     * GluonObservableList, using the default flags:
     * - LIST_WRITE_THROUGH: Changes in the local list are reflected to the remote copy of that list on Gluon Cloud.
     * - LIST_READ_THROUGH: Changes in the remote list on Gluon Cloud are reflected to the local copy of that list
     *
     * This means that any change done in any client app will be reflected in the cloud, and inmediatelly broadcasted
     * to all the listening applications.
     */
    public void retrieveComments() {
        GluonObservableList<Comment> retrieveList = DataProvider.<Comment>retrieveList(
                dataClient.createListDataReader(CLOUD_LIST_ID, Comment.class));
        
        retrieveList.stateProperty().addListener((obs, ov, nv) -> {
            if (ConnectState.SUCCEEDED.equals(nv)) {
                commentsList.set(retrieveList);
            }
        });
        
    }
    
    /**  
     * Add a new comment to the list
     * Note comments can be deleted directly on the ListView, since its bounded to the list
     * @param comment
     */
    public void addComment(Comment comment) {
        commentsList.get().add(comment);
    }
    
    /**
     *
     * @return a ListProperty, the wrapper of the remote list of comments.
     */
    public ListProperty<Comment> commentsProperty() {
        return commentsList;
    }
    
}
