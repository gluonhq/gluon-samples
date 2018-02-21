package com.gluonhq.deeplearning.linear;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends MobileApplication {

    public static final String TRAINING_VIEW = HOME_VIEW;

    @Override
    public void init() {
        addViewFactory(TRAINING_VIEW, () -> new TrainingView(TRAINING_VIEW));

        System.setProperty("java.vm.name", "gluonvm");
        System.setProperty("sun.arch.data.model", "64");
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
    }

}
