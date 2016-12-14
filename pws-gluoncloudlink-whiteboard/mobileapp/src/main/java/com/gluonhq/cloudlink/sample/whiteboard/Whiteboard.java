package com.gluonhq.cloudlink.sample.whiteboard;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.gluonhq.cloudlink.sample.whiteboard.views.BoardView;
import com.gluonhq.cloudlink.sample.whiteboard.views.EditView;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Whiteboard extends MobileApplication {

    public static final String BOARD_VIEW = HOME_VIEW;
    public static final String EDIT_VIEW = "Edit View";
    public static final String MENU_LAYER = "Side Menu";

    @Override
    public void init() throws Exception {
        addViewFactory(BOARD_VIEW, () -> (View) new BoardView().getView());
        addViewFactory(EDIT_VIEW, () -> (View) new EditView().getView());

        addLayerFactory(MENU_LAYER, () -> new SidePopupView(new DrawerManager().getDrawer()));
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.LIGHT_BLUE.assignTo(scene);

        ((Stage) scene.getWindow()).getIcons().add(new Image(Whiteboard.class.getResourceAsStream("/icon.png")));
    }
}
