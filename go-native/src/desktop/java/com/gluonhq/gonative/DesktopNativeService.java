/**
 * Copyright (c) 2016, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.gonative;

import com.github.sarxos.webcam.Webcam;
import com.gluonhq.charm.glisten.animation.ShakeTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DesktopNativeService implements NativeService {

    private final ObjectProperty<Image> fxImage = new SimpleObjectProperty<>(
            new Image(getClass().getResourceAsStream("/icon.png")));
    
    private final AnimationTimer timer;
    private long startTime = 0l;
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty z = new SimpleDoubleProperty();
    private final Random random;
    
    public DesktopNativeService() {
        random = new Random();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - startTime > 100_000_000) {
                    x.set(random.nextDouble() * 0.3);
                    y.set(random.nextDouble() * 0.4);
                    z.set(random.nextDouble() * 1.5);
                    startTime = now;
                }
            }
        };
    }
    
    @Override
    public void takePicture() {
        /**
         * Based on Sarxos project
         * https://github.com/sarxos/webcam-capture
         */
        new Thread(() -> {
            Webcam webcam = Webcam.getDefault();
            if (webcam != null) {
                try {
                    webcam.setViewSize(new Dimension(640, 480));
                } catch (IllegalArgumentException iae) {
                    System.out.println("Error: " + iae.getMessage());
                }
		webcam.open();
                BufferedImage bf = webcam.getImage();
                if (bf != null) {
                    WritableImage writableImage = SwingFXUtils.toFXImage(bf, null);
                    Platform.runLater(() -> fxImage.set(writableImage));
                }
                webcam.close();
            }
        }).start();
    }

    @Override
    public void retrievePicture() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        fileChooser.setTitle("Select a Picture...");

        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            fxImage.set(new Image("file://"+file.getAbsolutePath()));
        }
    }

    @Override
    public ObjectProperty<Image> imageProperty() {
        return fxImage;
    }
    
    @Override
    public void startAccelerometer() {
        timer.start();
    }
    
    @Override
    public void stopAccelerometer() {
        timer.stop();
    }

    @Override
    public DoubleProperty x() {
        return x;
    }

    @Override
    public DoubleProperty y() {
        return y;
    }

    @Override
    public DoubleProperty z() {
        return z;
    }

    @Override
    public void vibrate() {
        ShakeTransition shake = new ShakeTransition(MobileApplication.getInstance().getGlassPane());
        shake.playFromStart();
    }
}
