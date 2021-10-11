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
package com.gluonhq.samples.connect.file;

import com.gluonhq.attach.util.Services;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class Main extends Application {

    public static final File ROOT_DIR; 
    
    static {
        ROOT_DIR = Services.get(StorageService.class)
                    .flatMap(StorageService::getPrivateStorage)
                    .orElseThrow(() -> new RuntimeException("Error retrieving private storage"));
    }
    
    private static final String FILELIST_VIEW = HOME_VIEW;
    private static final String FILEOBJECT_VIEW = "FileObjectView";

    private final AppManager appManager = AppManager.initialize(this::postInit);

    public Main() {
        try {
            File languagesFile = new File(ROOT_DIR, "languages.json");
            if (!languagesFile.exists()) {
                try (FileWriter writer = new FileWriter(languagesFile)) {
                    writer.write("[\n" +
                            "  {\"name\":\"Java\",\"ratings\":20.956},\n" +
                            "  {\"name\":\"C\",\"ratings\":13.223},\n" +
                            "  {\"name\":\"C++\",\"ratings\":6.698},\n" +
                            "  {\"name\":\"C#\",\"ratings\":4.481},\n" +
                            "  {\"name\":\"Python\",\"ratings\":3.789}\n" +
                            "]");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File userFile = new File(ROOT_DIR, "user.json");
            if (!userFile.exists()) {
                try (FileWriter writer = new FileWriter(userFile)) {
                    writer.write("{\"name\":\"Duke\",\"subscribed\":true}");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void init() {
        appManager.addViewFactory(FILELIST_VIEW, () -> {
            try {
                return new FileListView();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        appManager.addViewFactory(FILEOBJECT_VIEW, () -> {
            try {
                return new FileObjectView();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        updateDrawer();
    }

    private void updateDrawer() {
        NavigationDrawer navigationDrawer = appManager.getDrawer();
        NavigationDrawer.Header header = new NavigationDrawer.Header("Gluon Mobile", "Gluon Connect File Provider Sample",
                new Avatar(21, new Image(getClass().getResourceAsStream("/icon.png"))));
        navigationDrawer.setHeader(header);
        NavigationDrawer.Item listItem = new NavigationDrawer.Item("List Viewer", MaterialDesignIcon.VIEW_LIST.graphic());
        NavigationDrawer.Item objectItem = new NavigationDrawer.Item("Object Viewer", MaterialDesignIcon.INSERT_DRIVE_FILE.graphic());
        navigationDrawer.getItems().addAll(listItem, objectItem);
        navigationDrawer.selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem.equals(listItem)) {
                appManager.switchView(FILELIST_VIEW);
            } else if (newItem.equals(objectItem)) {
                appManager.switchView(FILEOBJECT_VIEW);
            }
        });
    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
    }

    private void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
