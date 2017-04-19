package com.gluonhq.userauthentication;

import com.gluonhq.userauthentication.views.AppViewManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.layout.layer.SidePopupView;
import com.gluonhq.charm.glisten.visual.Swatch;
import com.gluonhq.cloudlink.client.user.UserClient;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class UserAuthentication extends MobileApplication {

    public static final String MENU_LAYER = "Side Menu";
    private UserClient authenticationClient;

    @Override
    public void init() {
        authenticationClient = new UserClient();

        AppViewManager.registerViews(this);
        addLayerFactory(MENU_LAYER, () -> new SidePopupView(new DrawerManager().getDrawer()));
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        scene.getStylesheets().add(UserAuthentication.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(UserAuthentication.class.getResourceAsStream("/icon.png")));
    }

    public UserClient getUserClient() {
        return authenticationClient;
    }
}
