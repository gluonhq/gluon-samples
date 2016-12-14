/**
 * Copyright (c) 2016, Gluon
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
package com.gluonhq.comments20.cloud;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.Cache;
import com.gluonhq.charm.down.plugins.CacheService;
import com.gluonhq.comments20.model.Comment;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.gluoncloud.GluonClient;
import com.gluonhq.connect.gluoncloud.SyncFlag;
import com.gluonhq.connect.gluoncloud.User;
import com.gluonhq.connect.provider.DataProvider;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javax.annotation.PostConstruct;

/** Service to access the application on Gluon Cloud, retrieve a list with comments
 * and send new comments to the list.
 * 
 */
public class Service {

    private GluonClient gluonClient;
    
    /*
    Every list stored under the same application on the Cloud has a unique id:
    */
    private static final String CLOUD_LIST_ID = "comments v2.0";
    
    /*
    An observable wrapper of the retrieved list, used to expose it and bind the 
    ListView items to the list.
    */
    private final ListProperty<Comment> commentsList = 
            new SimpleListProperty<>(FXCollections.<Comment>observableArrayList()); 
    
    /*
    The authenticated user. Check
    http://docs.gluonhq.com/charm/latest/#_user_authentication
    */
    private final ObjectProperty<User> user = new SimpleObjectProperty<>();
    
    /*
    Contains a comment that can be edited
    */
    private final ObjectProperty<Comment> activeComment = new SimpleObjectProperty<>();
    
    public ObjectProperty<Comment> activeCommentProperty() {
        return activeComment;
    }
    
    /**
     * Cache to manage avatar images
     */
    private static final Cache<String, Image> CACHE;
    
    static {
        CACHE = Services.get(CacheService.class)
                .map(cache -> cache.<String, Image>getCache("images"))
                .orElseThrow(() -> new RuntimeException("No CacheService available"));
    }
    
    public static Image getUserImage(String userPicture) {
        if (userPicture == null || userPicture.isEmpty()) {
            /**
             * https://commons.wikimedia.org/wiki/File:WikiFont_uniE600_-_userAvatar_-_blue.svg
             * By User:MGalloway (WMF) (mw:Design/WikiFont) [CC BY-SA 3.0 (http://creativecommons.org/licenses/by-sa/3.0)], via Wikimedia Commons
             */
            userPicture = Service.class.getResource("WikiFont_uniE600_-_userAvatar_-_blue.svg.png").toExternalForm();
        }
        // try to retrieve image from cache
        Image answer = CACHE.get(userPicture);
        if (answer == null) {
            // if not available yet, create new image from URL 
            answer = new Image(userPicture, true);
            // store it in cache
            CACHE.put(userPicture, answer);
        }
        return answer;
    }
    
    /**
     * See Afterburner.fx
     */
    @PostConstruct
    public void postConstruct() {
        gluonClient = GluonClientProvider.getGluonClient();
        user.bind(gluonClient.authenticatedUserProperty());
    }
    
    /**
     * Once there's a valid gluonClient, the contents of the list can be retrieved. This will return a 
     * GluonObservableList. Note the flags:
     * - LIST_WRITE_THROUGH: Changes in the local list are reflected to the remote copy of that list on Gluon Cloud.
     * - LIST_READ_THROUGH: Changes in the remote list on Gluon Cloud are reflected to the local copy of that list
     * - OBJECT_READ_THROUGH: Changes in observable properties of objects in the remote list on Gluon Cloud are reflected to the local objects of that list
     * - OBJECT_WRITE_THROUGH: Changes in the observable properties of objects in the local list are reflected to the remote copy on Gluon Cloud
     
     * This means that any change done in any client app will be reflected in the cloud, and inmediatelly broadcasted
     * to all the listening applications.
     */
    public void retrieveComments() {
        GluonObservableList<Comment> retrieveList = DataProvider.<Comment>retrieveList(
                gluonClient.createListDataReader(CLOUD_LIST_ID, 
                Comment.class,  
                SyncFlag.LIST_READ_THROUGH, SyncFlag.LIST_WRITE_THROUGH, 
                SyncFlag.OBJECT_READ_THROUGH, SyncFlag.OBJECT_WRITE_THROUGH));
        
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
    
    public User getUser() {
        return user.get();
    }
    
    public ObjectProperty<User> userProperty() {
        return user;
    }
}
