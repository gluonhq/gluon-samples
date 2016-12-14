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
package com.gluonhq.rubik.views;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.jpl.games.model.Moves;
import com.jpl.games.model.Rubik;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class RubikView extends View {

    private Rubik rubik;
    
    private LocalTime time = LocalTime.now();
    private Timeline timer;
    private final Label timeLabel;
    private final StringProperty clock = new SimpleStringProperty("00:00:00");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    
    private Moves moves = new Moves();
    
    public RubikView(String name) {
        super(name);
        
        rubik = new Rubik();
        setCenter(rubik.getSubScene());
        
        timer = new Timeline(
            new KeyFrame(Duration.ZERO, 
                e -> clock.set(LocalTime.now().minusNanos(time.toNanoOfDay()).format(fmt))),
            new KeyFrame(Duration.seconds(1)));
        
        timer.setCycleCount(Animation.INDEFINITE);
        timeLabel = new Label();
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 0.9em;");
        timeLabel.textProperty().bind(clock);
        
        rubik.isSolved().addListener((ov,b,b1)->{
            if(b1){
                timer.stop();
                MobileApplication.getInstance().showMessage("Solved in " + (rubik.getCount().get() + 1) + " movements!");
            }
        });
        
        getStylesheets().add(RubikView.class.getResource("rubik.css").toExternalForm());
        addEventHandler(MouseEvent.ANY, rubik.eventHandler);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        final Label label = new Label("Rubik's Cube");
        label.setStyle("-fx-text-fill: white; -fx-font-size: 1.2em;");
        
        final HBox hBox = new HBox(20, label, timeLabel);
        hBox.setAlignment(Pos.CENTER_LEFT);
        appBar.setTitle(hBox);
        appBar.getActionItems().addAll(MaterialDesignIcon.TRANSFORM.button(e -> {
            rubik.doReset();
            doScramble();
        }), MaterialDesignIcon.REFRESH.button(e -> rubik.doReset()));
    }
    
    // called from button Scramble
    private void doScramble() {
        rubik.doScramble();
        rubik.isOnScrambling().addListener((ov,v,v1)->{
            if(v && !v1){
                moves = new Moves();
                time = LocalTime.now();
                timer.playFromStart();
            }
        });
    }

    private void doReplay() {
        rubik.doReplay(moves.getMoves());
    }
    
}
