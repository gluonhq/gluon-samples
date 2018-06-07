package com.gluonhq.dl.mnist.app.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.PicturesService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.animation.BounceInDownTransition;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.dl.mnist.app.GluonMnistDL;
import com.gluonhq.dl.mnist.app.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.util.ResourceBundle;

import static com.gluonhq.charm.glisten.afterburner.DefaultDrawerManager.DRAWER_LAYER;
import com.gluonhq.dl.mnist.app.MnistImageView;
import com.gluonhq.dl.mnist.app.service.Service;
import static com.gluonhq.dl.mnist.app.views.AppViewManager.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

public class StartPresenter extends GluonPresenter<GluonMnistDL> {

    @FXML
    private View main;

    @FXML
    private Button buttonLoadImage;

    @FXML
    private Button buttonRunCloud;
    
    @FXML
    private Button buttonRunLocal;

    @FXML
    private Label labelStatus;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private ResourceBundle resources;
    
    @Inject
    private Model model;
    
    private MnistImageView imageView;
    

    public void initialize() {

        main.setShowTransitionFactory(BounceInDownTransition::new);

        buttonRunLocal.disableProperty().bind(model.nnModelProperty().isNull());

        main.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = getApp().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> getApp().showLayer(DRAWER_LAYER)));
                appBar.setTitleText("Federated Learning");

                if (model.getNnModel() == null) {
                    appBar.setProgressBarVisible(true);
                    appBar.setProgress(-1);
                }
                model.nnModelProperty().addListener((obs2, ov, nv) -> {
                    if (nv != null) {
                        appBar.setProgressBarVisible(false);
                        updateLabel();
                    }
                });
            }
        });
        
        imageView = new MnistImageView();
        imagePane.getChildren().add(imageView);
        
        // TODO: Remove?
        retrieveFakePicture();
    }

    @FXML
    void loadImage() {
        retrievePicture();
    }
    
    @FXML
    void runModel() {
        try {
            Service service = new Service();
            StringProperty answer = service.predictRemote(model, imageView.getImageFile());
            showResult(answer);
//            MATCH_VIEW.switchView();
//            Optional<Object> presenter = MATCH_VIEW.getPresenter();
//            MatchPresenter p = (MatchPresenter) presenter.get();
//            answer.addListener(new InvalidationListener() {
//                @Override
//                public void invalidated(Observable o) {
//                    String astring = answer.get();
//                    System.out.println("got answer: "+astring);
//                    if (astring != null) {
//                        p.setResult(Integer.valueOf(astring));
//                    }
//                }
//            });
//            if (answer.get() != null) {
//                p.setResult(Integer.valueOf(answer.get()));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void runLocalModel() {
        try {
            Service service = new Service();
            StringProperty answer = service.predictLocal(model, model.getCurrentImageFile());
        //            InputStream sixStream = StartPresenter.class.getResourceAsStream("/six.png");
//StringProperty answer = service.predictLocalInputStream(model, sixStream);
            showResult(answer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResult (StringProperty answer) {
          MATCH_VIEW.switchView();
            Optional<Object> presenter = MATCH_VIEW.getPresenter();
            MatchPresenter p = (MatchPresenter) presenter.get();
            p.setResult(-1);
            answer.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable o) {
                    String astring = answer.get();
                    System.out.println("got answer: "+astring);
                    if (astring != null) {
                        p.setResult(Integer.valueOf(astring));
                    }
                }
            });
            if (answer.get() != null) {
                p.setResult(Integer.valueOf(answer.get()));
            }
    }
    private void updateLabel() {
        labelStatus.setText(resources.getString(model.getNnModel() != null? "label.loaded.text": "label.loading.text"));
    }

    private void retrieveFakePicture() {
        InputStream sixStream = StartPresenter.class.getResourceAsStream("/six.png");
        Image im = new Image(sixStream);//, 28d,28d,true, true);
        imageView.updateImage(main, im);
        try {
            model.setCurrentImageFile(imageView.getImageFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void retrievePicture() {
        
        final Optional<PicturesService> pictureService = Services.get(PicturesService.class);
        if (pictureService.isPresent()) {
            pictureService.get().takePhoto(false).ifPresent(photo -> imageView.updateImage(main, photo));
        } else {
            loadImageFile().ifPresent(photo -> imageView.updateImage(main, photo));
        }
        try {
            model.setCurrentImageFile(imageView.getImageFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StartPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Optional<Image> loadImageFile() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Photo File Chooser");

        File file = fileChooser.showOpenDialog(main.getScene().getWindow());

        return Optional.ofNullable(file).map( f -> new Image(f.toURI().toString()));

    }
    
}
