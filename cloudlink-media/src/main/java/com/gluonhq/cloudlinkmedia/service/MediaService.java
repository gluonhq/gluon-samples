/**
 * Copyright (c) 2017 Gluon
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
package com.gluonhq.cloudlinkmedia.service;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.BrowserService;
import com.gluonhq.charm.down.plugins.LifecycleEvent;
import com.gluonhq.charm.down.plugins.LifecycleService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.cloudlink.client.media.MediaClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class MediaService {
    
    private static final String REMOTE_IMAGE = "GluonBanner";
    private static final String POPUP_NAME = "SidePopupLayer";
    
    private MediaClient mediaClient = new MediaClient();
    private ImageView imageView;

    private ScheduledExecutorService scheduler;

    public MediaService() {
        
        MobileApplication.getInstance().addLayerFactory(POPUP_NAME, () -> {
            imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);
            HBox adsBox = new HBox(imageView);
            adsBox.getStyleClass().add("mediaBox");
            return new SidePopupView(adsBox, Side.BOTTOM, false);
        });
        
        Services.get(LifecycleService.class).ifPresent(service -> {
            service.addListener(LifecycleEvent.PAUSE, this::stopExecutor);
            service.addListener(LifecycleEvent.RESUME, this::startExecutor);
        });
        startExecutor();
    }
        
    private void startExecutor() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                MobileApplication.getInstance().hideLayer(POPUP_NAME); 
                imageView.setImage(getMedia());
                MobileApplication.getInstance().showLayer(POPUP_NAME);
            }); 
        }, 2, 20, TimeUnit.SECONDS);
        
    }
    
    public void stopExecutor() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
    
    private Image getMedia() {
        try {
            return mediaClient.loadImage(REMOTE_IMAGE);
        } catch (IOException ex) {
            System.out.println("Error loading " + REMOTE_IMAGE + ": " + ex);
        }
        return null;
    }
    
}
