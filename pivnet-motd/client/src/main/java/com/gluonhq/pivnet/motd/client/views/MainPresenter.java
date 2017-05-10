package com.gluonhq.pivnet.motd.client.views;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.pivnet.motd.client.MessageOfTheDay;
import com.gluonhq.pivnet.motd.client.service.Service;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.inject.Inject;

public class MainPresenter extends GluonPresenter<MessageOfTheDay> {

    @Inject
    private Service service;

    @FXML
    private View main;

    @FXML
    private Label lblMotd;

    @FXML
    private ResourceBundle resources;
    
    public void initialize() {
        main.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText(resources.getString("message.text"));
            }
        });
        lblMotd.textProperty().bind(service.retrieveMOTD());
    }
}
