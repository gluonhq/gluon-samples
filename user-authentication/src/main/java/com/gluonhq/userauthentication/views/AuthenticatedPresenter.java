package com.gluonhq.userauthentication.views;

import com.gluonhq.userauthentication.UserAuthentication;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.client.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class AuthenticatedPresenter extends GluonPresenter<UserAuthentication> {

    @FXML
    private View authenticatedView;

    @FXML
    GridPane gridPane;

    @FXML
    Button signOutButton;

    public void initialize() {

        GridPane.setHalignment(signOutButton, HPos.CENTER);
        authenticatedView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().showLayer(UserAuthentication.MENU_LAYER)));
                appBar.setTitleText("Authenticated");
                loadDetails();
            }
        });
    }

    public void loadDetails() {
        User user = getApp().getUserClient().getAuthenticatedUser();
        if (user != null) {
            Label name = new Label(user.getName());
            Label username = new Label(user.getNick());
            ImageView picture = new ImageView(new Image(user.getPicture(), true));
            picture.setFitHeight(64);
            picture.setFitWidth(64);
            gridPane.add(username, 2, 1);
            gridPane.add(name, 2, 2);
            gridPane.add(picture, 2, 3);
        }
    }

    @FXML
    public void signOut(ActionEvent actionEvent) {
        getApp().getUserClient().signOut();
        AppViewManager.PRIMARY_VIEW.switchView();
    }
}
