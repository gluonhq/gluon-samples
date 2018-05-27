package com.gluonhq.dl.mnist.app.views;

import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.FlipInYTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.dl.mnist.app.GluonMnistDL;
import com.gluonhq.dl.mnist.app.Model;
import com.gluonhq.dl.mnist.app.service.Service;
import java.io.File;
import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import javax.inject.Inject;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IncorrectMatchPresenter extends GluonPresenter<GluonMnistDL> {

    @FXML
    private View incorrectView;

    @FXML
    private ImageView imageView;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private Button submitButton;

    @FXML
    private ToggleGroup toggleGroup;
    
    @Inject
    private Model model;

    @Inject
    private Service service;
    private Image filteredImage;
    
    public void initialize() {

        incorrectView.setShowTransitionFactory(FlipInYTransition::new);

        toggleGroup.selectToggle(null);

        BooleanBinding toggleNotSelected = Bindings.createBooleanBinding(
                () -> toggleGroup.getSelectedToggle() == null,
                toggleGroup.selectedToggleProperty()
        );
        submitButton.disableProperty().bind(toggleNotSelected);
     //   imageView.imageProperty().bind(model.filteredImageProperty());
        incorrectView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setTitleText("Incorrect Match");
                appBar.setNavIcon(MaterialDesignIcon.ERROR.button());
            }
        });
    }

    @FXML
    public void submitResults() {
        throw new RuntimeException ("don't submit results yet");
//
//        int correctedNumber = Integer.parseInt(((ToggleButton) toggleGroup.getSelectedToggle()).getText());
//
//        FileUtils
//            .getImageFile(model.getFilteredImage(), String.valueOf(correctedNumber))
//            .ifPresent( imageFile -> {
//                try {
//                    service.singleTrain(model, imageFile.getParentFile());
//                } catch (IOException ex) {
//                    System.out.println("Error " + ex);
//                }
//            });

//        File imageFile = FileUtils.getImageFile(model.getFilteredImage(), String.valueOf(correctedNumber));
//            if (imageFile != null) {
//                try {
//                    service.singleTrain(model, imageFile.getParentFile());
//                } catch (IOException ex) {
//                    System.out.println("Error " + ex);
//                }
//            }
        //then switch to home

//        getApp().goHome();
//        getApp().showMessage("Model was corrected and updated" );

    }

    @FXML
    public void cancel() {
        getApp().goHome();
    }

}
