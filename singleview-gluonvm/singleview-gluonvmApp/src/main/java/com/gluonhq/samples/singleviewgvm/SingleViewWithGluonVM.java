package com.gluonhq.samples.singleviewgvm;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SingleViewWithGluonVM extends MobileApplication {

    public static final String BASIC_VIEW = HOME_VIEW;

    @Override
    public void init() {
        addViewFactory(BASIC_VIEW, () -> new BasicView(BASIC_VIEW));
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(SingleViewWithGluonVM.class.getResourceAsStream("/icon.png")));
    }
}
