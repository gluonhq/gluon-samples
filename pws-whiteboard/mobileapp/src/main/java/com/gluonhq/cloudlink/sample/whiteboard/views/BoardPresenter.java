package com.gluonhq.cloudlink.sample.whiteboard.views;

import com.gluonhq.charm.glisten.animation.BounceInLeftTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.sample.whiteboard.Whiteboard;
import com.gluonhq.cloudlink.sample.whiteboard.model.Item;
import com.gluonhq.cloudlink.sample.whiteboard.model.Model;
import com.gluonhq.cloudlink.sample.whiteboard.service.Service;
import com.gluonhq.connect.GluonObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;

public class BoardPresenter {

    @Inject private Service service;
    @Inject private Model model;

    @FXML private View board;

    @FXML private ListView<Item> lstItems;

    public void initialize() {
        board.setShowTransitionFactory(BounceInLeftTransition::new);
        board.showingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(event -> MobileApplication.getInstance().showLayer(Whiteboard.MENU_LAYER)));
                appBar.setTitleText("Whiteboard");
            }
        });

        final FloatingActionButton floatingActionButton = new FloatingActionButton();
        floatingActionButton.setOnAction(e -> edit(null));
        board.getLayers().add(floatingActionButton.getLayer());

        GluonObservableList<Item> items = service.retrieveItems();
        lstItems.setItems(items);
        lstItems.setCellFactory(p -> new ItemCell(service, this::edit, this::remove));
        lstItems.setPlaceholder(new Label("The whiteboard is empty."));

    }

    private void edit(Item item) {
        model.activeItem().set(item);
        MobileApplication.getInstance().switchView(Whiteboard.EDIT_VIEW);
    }

    private void remove(Item item) {
        model.getItems().remove(item);
    }
}
