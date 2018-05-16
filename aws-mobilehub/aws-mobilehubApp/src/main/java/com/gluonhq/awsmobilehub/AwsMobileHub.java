package com.gluonhq.awsmobilehub;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AwsMobileHub extends MobileApplication {

    @Override
    public void init() {
        addViewFactory(HOME_VIEW, () -> new BasicView());
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(AwsMobileHub.class.getResourceAsStream("/icon.png")));
    }
    
    @Override
    public void stop() throws Exception {
        // kill aws threads
        System.exit(0);
    }
}
