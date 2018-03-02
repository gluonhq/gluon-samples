package com.gluonhq.peepsat;

import com.gluonhq.peepsat.views.AppViewManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class PeepsAt extends MobileApplication {

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.PURPLE.assignTo(scene);

        scene.getStylesheets().add(PeepsAt.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(PeepsAt.class.getResourceAsStream("/icon.png")));
    }
}
