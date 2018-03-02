package com.gluonhq.peepsat.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.PositionService;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.peepsat.Model;
import com.gluonhq.peepsat.PeepsAt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ResourceBundle;


public class RegisterPresenter extends GluonPresenter<PeepsAt> {
    
    @FXML
    View register;
    
    @FXML
    TextField username;

    @FXML
    private ResourceBundle resources;
    private PositionService positionService;

    public void initialize() {
        
        register.setOnShown(event -> {
            register.requestFocus();
            SettingsService settingsService = Services.get(SettingsService.class).get();
            final String myname = settingsService.retrieve("myname");
            if (myname != null) {
                Model.username = myname;
                AppViewManager.MAP_VIEW.switchView();
            }
        });
        
        register.setOnShowing(event -> {
            AppBar appBar = getApp().getAppBar();
            appBar.setTitleText(resources.getString("title"));
        });
    }
    
    @FXML
    public void signUp(ActionEvent event) {
        SettingsService settingsService = Services.get(SettingsService.class).get();
        settingsService.store("myname", username.getText());
        Model.username = username.getText();
        AppViewManager.MAP_VIEW.switchView();
    }

}
