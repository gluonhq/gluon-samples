/*
 * Copyright (c) 2017, 2018 Gluon
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
package com.gluonhq.comments20.views;

import com.airhacks.afterburner.injection.Injector;
import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.DisplayService;
import com.gluonhq.charm.glisten.afterburner.AppView;
import com.gluonhq.charm.glisten.afterburner.AppViewRegistry;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.afterburner.Utils;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.comments20.cloud.Service;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.util.Collection;
import java.util.Locale;

import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.*;

public class AppViewManager {

    public static final AppViewRegistry REGISTRY = new AppViewRegistry();
    private static Avatar avatar;
    
    public static final AppView COMMENTS_VIEW = view("Comments", CommentsPresenter.class, MaterialDesignIcon.COMMENT, SHOW_IN_DRAWER, HOME_VIEW, SKIP_VIEW_STACK);
    public static final AppView EDITION_VIEW = view("Edition", EditionPresenter.class, MaterialDesignIcon.EDIT, SHOW_IN_DRAWER);
    
    private static AppView view(String title, Class<? extends GluonPresenter<?>> presenterClass, MaterialDesignIcon menuIcon, AppView.Flag... flags ) {
        return REGISTRY.createView(name(presenterClass), title, presenterClass, menuIcon, flags);
    }

    private static String name(Class<? extends GluonPresenter<?>> presenterClass) {
        return presenterClass.getSimpleName().toUpperCase(Locale.ROOT).replace("PRESENTER", "");
    }
    
    public static void registerViewsAndDrawer(MobileApplication app) {
        for (AppView view : REGISTRY.getViews()) {
            view.registerView(app);
        }

        avatar = new Avatar();
        Services.get(DisplayService.class).ifPresent(d -> {
            if (d.isTablet()) {
                avatar.getStyleClass().add("tablet");
            }
        });
        
        NavigationDrawer.Header header = new NavigationDrawer.Header("Gluon Mobile",
                "The Comments App", avatar);
        DrawerManager.buildDrawer(app, header, REGISTRY.getViews()); 
    }
    
    private static class DrawerManager {
        
        static void buildDrawer(MobileApplication app, NavigationDrawer.Header header, Collection<AppView> views) {
            final NavigationDrawer drawer = app.getDrawer();
            Utils.buildDrawer(drawer, header, views);

            final Service service = Injector.instantiateModelOrService(Service.class);
            
            for (Node item : drawer.getItems()) {
                if (item instanceof NavigationDrawer.ViewItem && 
                        ((NavigationDrawer.ViewItem) item).getViewName().equals(EDITION_VIEW.getId())) {
                    item.disableProperty().bind(service.userProperty().isNull());
                    break;
                }
            }
            
            service.userProperty().addListener((obs, ov, nv) -> avatar.setImage(getAvatarImage(service)));
            avatar.setImage(getAvatarImage(service));
        }
        
        private static Image getAvatarImage(Service service) {
            if (service != null && service.userProperty().get() != null) {
                return Service.getUserImage(service.userProperty().get().getPicture());
            } 
            return new Image(AppViewManager.class.getResourceAsStream("/icon.png"));
        }
    }
}
