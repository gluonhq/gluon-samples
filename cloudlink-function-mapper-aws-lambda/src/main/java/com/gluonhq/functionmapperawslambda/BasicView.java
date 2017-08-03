package com.gluonhq.functionmapperawslambda;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.AudioRecordingService;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Icon;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunction;
import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BasicView extends View {

    public BasicView(String name) {
        super(name);

        AudioRecordingService audioRecordingService = Services.get(AudioRecordingService.class)
                .orElseThrow(() -> new RuntimeException("Could not find Audio Recording service."));

        Button start = new Button("Start Recording", new Icon(MaterialDesignIcon.MIC));
        start.setOnAction(e -> {
            audioRecordingService.clearAudioFolder();
            audioRecordingService.startRecording(8000.0f, 16, 2, 60);
        });
        start.disableProperty().bind(audioRecordingService.recordingProperty());

        Button stop = new Button("Stop Recording", new Icon(MaterialDesignIcon.STOP));
        stop.setOnAction(e -> audioRecordingService.stopRecording());
        stop.disableProperty().bind(audioRecordingService.recordingProperty().not());

        DataClient dataClient = DataClientBuilder.create().build();

        TextArea responseDebug = new TextArea();

        Button upload = new Button("Upload Audio", new Icon(MaterialDesignIcon.CLOUD_UPLOAD));
        upload.setOnAction(e -> {
            responseDebug.clear();
            for (String audioChunk : audioRecordingService.getAudioChunkFiles()) {
                File audioChunkFile = new File(audioRecordingService.getAudioFolder(), audioChunk);
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                     FileInputStream fis = new FileInputStream(audioChunkFile)) {
                    int bytesRead;
                    byte[] bytes = new byte[4096];
                    while ((bytesRead = fis.read(bytes)) != -1) {
                        baos.write(bytes, 0, bytesRead);
                    }

                    RemoteFunction storeAudio = RemoteFunctionBuilder.create("storeAudio")
                            .param("s3Key", audioChunk)
                            .rawBody(baos.toByteArray())
                            .build();

                    GluonObservableObject<String> response = DataProvider.retrieveObject(dataClient.createObjectDataReader(storeAudio, String.class));
                    response.stateProperty().addListener((obs, ov, nv) -> {
                        if (nv == ConnectState.FAILED) {
                            response.getException().printStackTrace();
                        } else if (nv == ConnectState.SUCCEEDED) {
                            responseDebug.appendText("Audio File " + audioChunk + " successfully uploaded to Amazon S3 bucket.");
                            responseDebug.appendText("\nResponse from AWS Lambda was:");
                            responseDebug.appendText("\n" + response.get());
                            responseDebug.appendText("\n===================");
                        }
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        upload.disableProperty().bind(audioRecordingService.recordingProperty()
                .or(audioRecordingService.getAudioChunkFiles().emptyProperty()));

        VBox controls = new VBox(15.0, start, stop, upload, responseDebug);
        controls.setAlignment(Pos.CENTER);

        setCenter(controls);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> System.out.println("Menu")));
        appBar.setTitleText("Basic View");
        appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> System.out.println("Search")));
    }

}
