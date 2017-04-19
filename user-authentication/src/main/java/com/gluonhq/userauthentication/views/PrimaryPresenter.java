package com.gluonhq.userauthentication.views;

import com.gluonhq.userauthentication.UserAuthentication;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.client.user.UserClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

public class PrimaryPresenter extends GluonPresenter<UserAuthentication> {

    @FXML
    private View primary;

    @FXML
    private Label label;

    @FXML
    private ResourceBundle resources;

    public void initialize() {
        // switch to authenticated view when we have an authenticated user
        if (getApp().getUserClient().isAuthenticated()) {
            Platform.runLater(this::loadAuthenticatedView);
        }

        primary.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        getApp().showLayer(UserAuthentication.MENU_LAYER)));
                appBar.setTitleText("Primary");
                appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e ->
                        System.out.println("Search")));
            }
        });
    }

    @FXML
    private void buttonClick() {
        UserClient userClient = getApp().getUserClient();
        if (userClient.isAuthenticated()) {
            loadAuthenticatedView();
        } else {
            userClient.authenticate(run -> loadAuthenticatedView());
        }
    }

    private void loadAuthenticatedView() {
        AppViewManager.AUTHENTICATED_VIEW.switchView();
    }

}
