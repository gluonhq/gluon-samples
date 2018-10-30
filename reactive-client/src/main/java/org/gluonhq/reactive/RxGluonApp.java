package org.gluonhq.reactive;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RxGluonApp extends MobileApplication {

    @Override
    public void init() {
        addViewFactory(HOME_VIEW, BasicView::new);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.AMBER.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(RxGluonApp.class.getResourceAsStream("/icon.png")));
    }
}
