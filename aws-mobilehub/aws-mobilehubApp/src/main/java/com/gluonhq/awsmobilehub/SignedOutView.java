package com.gluonhq.awsmobilehub;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SignedOutView extends View {

    public SignedOutView() {
        
        Label label = new Label("Gluon - AWS Mobile Hub");

        Button signIn = new Button("Sign In");
        signIn.setGraphic(new Icon(MaterialDesignIcon.VPN_LOCK));
        signIn.setOnAction(e -> AWSService.getInstance().signIn());
        
        VBox controls = new VBox(15.0, label, signIn);
        controls.setAlignment(Pos.CENTER);
        
        setCenter(controls);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("AWS Mobile Hub");
    }
    
}
