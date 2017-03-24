package com.gluonhq.demos.tictactoe;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class TicTacToe extends MobileApplication {

    private static final String BASIC_VIEW = HOME_VIEW;

    private static String HOST =  null;
    private static final String HOST_KEY = "tic.tac.toe.url";

    @Override
    public void init() {
        addViewFactory(BASIC_VIEW, () -> new MainView(BASIC_VIEW));
    }

    @Override
    public void postInit(Scene scene) {

        Swatch.BLUE_GREY.assignTo(scene);

        scene.getStylesheets().add(getClass().getResource("/tictactoe.css").toExternalForm());

        ((Stage) scene.getWindow()).getIcons().add(new Image(TicTacToe.class.getResourceAsStream("/icon.png")));

    }

    private static Optional<SettingsService> SETTINGS_SERVICE = Services.get(SettingsService.class);

    static final String getHost() {
        if ( HOST == null ) {
            SETTINGS_SERVICE.ifPresent(service -> HOST =  service.retrieve(HOST_KEY));
        }
        return HOST == null? "http://localhost:8090": HOST;
    }

    static final void setHost( String newHost ) {
        Services.get(SettingsService.class).ifPresent(service -> {
            service.store(HOST_KEY, newHost);
        });
        HOST = newHost;
    }
}
