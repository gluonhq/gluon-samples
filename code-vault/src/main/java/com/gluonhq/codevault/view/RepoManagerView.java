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
package com.gluonhq.codevault.view;

import com.gluonhq.codevault.git.GitRepoException;
import com.gluonhq.codevault.git.GitRepository;
import com.gluonhq.particle.annotation.ParticleView;
import com.gluonhq.particle.state.StateManager;
import com.gluonhq.particle.view.View;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import org.controlsfx.dialog.ExceptionDialog;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ParticleView(name = "repoManager", isDefault = true)
public class RepoManagerView implements View {

    private static final Logger LOGGER = Logger.getLogger(RepoManagerView.class.getName());

    @Inject private StateManager stateManager;

    private final TabPane tabs = new TabPane();

    @Override
    public void init() {
        stateManager.setPersistenceMode(StateManager.PersistenceMode.USER);
        tabs.setSide(Side.BOTTOM);

        stateManager.getPropertyAsString("repoLocations").ifPresent(s -> {
            String locations = s.trim();
            if (locations.length() > 0) {
                for (String location : locations.split(",")) {
                    openRepo(new File(location));
                }
            }
        });
    }

    @Override
    public Node getContent() {
        return tabs;
    }

    public void openRepo(File location) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            final GitRepository repo = new GitRepository(location);

            // if open already - select it
            Tab tab = isRepoOpen(repo);
            if (tab == null) {
                fxmlLoader.setRoot(null);
                fxmlLoader.setLocation(RepoLogController.class.getResource("repolog.fxml"));

                Node content = fxmlLoader.load();
                RepoLogController controller = fxmlLoader.getController();
                controller.setRepository(repo);

                tab = new Tab(repo.getName(), content);
                tab.setGraphic(getGitIcon());
                tab.setUserData(repo);
                tab.setTooltip(new Tooltip(repo.getLocation().toString()));

                tabs.getTabs().add(tab);
            }
            tabs.getSelectionModel().select(tab);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can not load FXML file '" + fxmlLoader.getLocation() + "'", e);
        } catch (GitRepoException e) {
            ExceptionDialog dlg = new ExceptionDialog(e);
            dlg.initOwner(tabs.getScene().getWindow());
            dlg.setHeaderText(null);
            dlg.showAndWait();
        }
    }

    private Tab isRepoOpen(GitRepository repo) {
        Objects.requireNonNull(repo);
        for(Tab tab : tabs.getTabs()) {
            if (repo.equals(tab.getUserData())) {
                return tab;
            }
        }
        return null;
    }

    private Glyph getGitIcon() {
        return GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.GIT_SQUARE);
    }

    @Override
    public void dispose() {
        String locations = tabs.getTabs().stream()
                .map(tab -> {
                    GitRepository repo = (GitRepository) tab.getUserData();
                    try {
                        return repo.getLocation().toString();
                    } finally {
                        repo.close();
                    }
                })
                .collect(Collectors.joining(","));

        stateManager.getStateIO().setProperty("repoLocations", locations);
    }
}
