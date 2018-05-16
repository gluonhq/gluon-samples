package com.gluonhq.awsmobilehub;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class BasicView extends View {

    public BasicView() {
        
        AWSService awsService = new AWSService();
        
        Label label = new Label("Gluon - AWS Mobile Hub");

        Button upload = new Button("Upload File");
        upload.setGraphic(new Icon(MaterialDesignIcon.CLOUD_UPLOAD));
        upload.setOnAction(e -> awsService.uploadFile("/assets/localFile.txt"));
        
        Button download = new Button("Download File");
        download.setGraphic(new Icon(MaterialDesignIcon.CLOUD_DOWNLOAD));
        download.setOnAction(e -> awsService.downloadFile("/public/example-image.png"));
        
        VBox controls = new VBox(15.0, label, upload, download);
        controls.setAlignment(Pos.CENTER);
        
        setCenter(controls);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("AWS Mobile Hub");
    }
    
}
