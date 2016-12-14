package com.gluonhq.cloudlink.sample.whiteboard.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.sample.whiteboard.Whiteboard;
import com.gluonhq.cloudlink.sample.whiteboard.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.inject.Inject;

public class EditPresenter {

    @Inject private Model model;

    @FXML private View edit;

    @FXML private Button submit;
    @FXML private Button cancel;
    @FXML private TextField title;

    public void initialize() {
        edit.setShowTransitionFactory(BounceInRightTransition::new);

        edit.showingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                Item activeItem = model.activeItem().get();
                if (activeItem != null) {
                    submit.setText("APPLY");
                    title.setText(activeItem.getTitle());
                } else {
                    submit.setText("SUBMIT");
                }

                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(event -> MobileApplication.getInstance().showLayer(Whiteboard.MENU_LAYER)));
                appBar.setTitleText(model.activeItem().get() == null ? "Add Item" : "Edit Item");
            }
        });

        submit.disableProperty().bind(title.textProperty().isEmpty());
        submit.setOnAction(event -> {
            Item item = model.activeItem().get() == null ? new Item() : model.activeItem().get();
            item.setTitle(title.getText());
            if (model.activeItem().get() == null) {
                model.getItems().add(item);
            }
            close();
        });

        cancel.setOnAction(event -> close());
    }

    private void close() {
        title.clear();
        model.activeItem().set(null);

        MobileApplication.getInstance().goHome();
    }
}
