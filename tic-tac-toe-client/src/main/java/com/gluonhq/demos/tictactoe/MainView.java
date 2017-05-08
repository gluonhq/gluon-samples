package com.gluonhq.demos.tictactoe;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.layout.layer.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MainView extends View {

    private Board board = new Board();

    public MainView(String name) {

        super(name);
        getStyleClass().add("game-view");

        setCenter(board);

        FloatingActionButton fab = new FloatingActionButton(
                MaterialDesignIcon.REFRESH.text,
                e -> board.restart());

        getLayers().add(fab.getLayer());


    }
   

    @Override
    protected void updateAppBar(AppBar appBar) {
//        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("Tic Tac Toe");
        appBar.getActionItems().addAll(MaterialDesignIcon.SETTINGS.button(e -> updateHost()));
//        appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> System.out.println("Search")));
    }


    private void updateHost() {

        Dialog<String> dialog = new Dialog<String>("Host", null);

        TextField hostField = new TextField(TicTacToe.getHost());
        dialog.setContent(hostField);

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            dialog.setResult(hostField.getText());
            dialog.hide();
        });

        BooleanBinding urlBinding = Bindings.createBooleanBinding(
                () -> isUrlValid(hostField.getText()),
                hostField.textProperty());
        okButton.disableProperty().bind(urlBinding.not());

        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(e -> dialog.hide());
        dialog.getButtons().addAll(okButton,cancelButton);
        dialog.showAndWait().ifPresent( url -> TicTacToe.setHost(url));

    }



    private boolean isUrlValid( String url ){
        if ( url == null || url.trim().isEmpty() ) return false;
        try {
            URL u = new URL(url); // this would check for the protocol
            u.toURI();            // does the extra checking required for validation of URI
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
    
}
