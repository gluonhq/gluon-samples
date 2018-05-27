package com.gluonhq.dl.mnist.app.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.FlipInYTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.dl.mnist.app.GluonMnistDL;
import com.gluonhq.dl.mnist.app.Model;
import com.gluonhq.dl.mnist.app.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.util.ResourceBundle;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MatchPresenter extends GluonPresenter<GluonMnistDL> {

    @FXML
    private View matchView;

    @FXML
    private Label labelStatus;

    @FXML
    private ResourceBundle resources;
    
    @Inject
    private Model model;

    @Inject
    private Service service;
    
    private int result;

    public void initialize() {

        matchView.setShowTransitionFactory(FlipInYTransition::new);

        matchView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.STAR.button());
                appBar.setTitleText("Match Found");

                labelStatus.setText( "This is " + getResult() + "!"); //TODO Assign the resulting number
            }
        });
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
        labelStatus.setText( "This is " + getResult() + "!");
    }


    @FXML
    public void onIncorrectMatch() {
        AppViewManager.INCORRECT_VIEW.switchView();
    }

    @FXML
    public void onCorrectMatch() {
        getApp().goHome();
    }

}
