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
package com.gluonhq.comments20.views;

import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * This control is a wrapper of a {@link ListTile}
 * The ListTile node has to be provided, and mouse events listeners are added
 * allowing a swipe gesture to move the listTile to the right or left.
 * 
 * Behind the tile an HBox is added, with two white {@link Icon} on the 
 * left and right. By default, those are {@link MaterialDesignIcon#DELETE} icon on
 * the left and  {@link MaterialDesignIcon#EDIT} icon on the right.
 * 
 * After the swipe events finishes, if the distance of the translation is greater
 * than a certain threshold, a swiped boolean is set to true. Otherwise, the tile
 * is returned to its initial position with a {@link TranslateTransition}
 * 
 * To keep scrolling the listView and sliding the ListCell separated, two boolean
 * properties track each movement
 * 
 */
public class SlidingListTile extends StackPane {

    private final ListTile tile;
    private double iniX = 0d, iniY = 0d;
    
    /**
     * Minimun distance that listTile has to be slid to trigger swipe fake effect
     */
    private final DoubleProperty threshold = new SimpleDoubleProperty(150);
    /**
     * Visual effect applied to icons depending on  listTile allowed or not allowed
     */
    private final BooleanProperty allowed = new SimpleBooleanProperty(true);
    
    /**
     * Sliding the tile horizontally or scrolling the listview vertically
     */
    private final BooleanProperty scrolling = new SimpleBooleanProperty();
    private final BooleanProperty sliding = new SimpleBooleanProperty();
    
    /**
     * fake swipe events, in terms of boolean properties
     */
    private final BooleanProperty swipedLeft = new SimpleBooleanProperty();
    private final BooleanProperty swipedRight = new SimpleBooleanProperty();
    
    /*
    text behind the list tile, on left and right positions
    */
    private final StringProperty textLeft = new SimpleStringProperty();
    private final StringProperty textRight = new SimpleStringProperty();
    
    /**
     * Creates a new sliding list tile 
     * @param tile the ListTile on top 
     * @param allowed icons are enabled if allowed, or disabled otherwise
     * @param textLeft the icon on the left
     * @param textRight the icon on the right
     */
    public SlidingListTile(ListTile tile, boolean allowed, String textLeft, String textRight) {
        
        this.textLeft.set(textLeft);
        this.textRight.set(textRight);
        
        /*
        Back HBox
        */
        HBox backPane = new HBox();
        backPane.setAlignment(Pos.CENTER);
        backPane.setPadding(new Insets(10));
        backPane.getStyleClass().add("sliding");
        
        // sliding from right to left
        PseudoClass pseudoClassLeft = PseudoClass.getPseudoClass("left");
        tile.translateXProperty().addListener((obs, ov, nv) ->
                backPane.pseudoClassStateChanged(pseudoClassLeft, nv.doubleValue() < 0));
        
        Label labelLeft = new Label(this.textLeft.get());
        labelLeft.getStyleClass().add("icon-text");
        backPane.getChildren().add(labelLeft);
        
        HBox gap = new HBox();
        HBox.setHgrow(gap, Priority.ALWAYS);
        backPane.getChildren().add(gap);
        
        Label labelRight = new Label(this.textRight.get());
        labelRight.getStyleClass().add("icon-text");
        backPane.getChildren().add(labelRight);
        
        this.allowed.addListener((obs, ov, nv) -> {
            if (nv) {
                if (labelLeft.getStyleClass().contains("not-allowed")) {
                    labelLeft.getStyleClass().remove("not-allowed");
                }
                if (labelRight.getStyleClass().contains("not-allowed")) {
                    labelRight.getStyleClass().remove("not-allowed");
                }
            } else {
                if (!labelLeft.getStyleClass().contains("not-allowed")) {
                    labelLeft.getStyleClass().add("not-allowed");
                }
                if (!labelRight.getStyleClass().contains("not-allowed")) {
                    labelRight.getStyleClass().add("not-allowed");
                }
            }
        });
        this.allowed.set(allowed);
        
        /*
        Front ListTile
        */
        this.tile = tile;
        tile.getStyleClass().add("tile");
        
        /*
         Listen to mouse events, to generate fake swipe events 
         */
        tile.setOnMousePressed(e -> {
            iniX=e.getSceneX();
            iniY=e.getSceneY();
            swipedLeft.set(false);
            swipedRight.set(false);
        });
        
        tile.setOnMouseDragged(e -> {
            // once sliding or scrolling have started, no change can be done 
            // until release and start again
            if (scrolling.get() || (!sliding.get() && Math.abs(e.getSceneY() - iniY) > 10)) {
                e.consume();
                scrolling.set(true);
                
            } 
            if (sliding.get() || (!scrolling.get() && Math.abs(e.getSceneX() - iniX) > 10)) {
                sliding.set(true);
                if (e.getSceneX() - iniX >= -tile.getWidth() + 20 && 
                        e.getSceneX() - iniX <= tile.getWidth() - 20) {
                    translateTile(e.getSceneX() - iniX);
                }
            }
        });
        
        // on mouse release, if sliding, generate swipe event
        tile.setOnMouseReleased(e->{
            if (scrolling.get()) {
                e.consume();
            } 
            
            if (sliding.get()) {
                // on sliding, swipe event after slid distance greater than threshold
                if (e.getSceneX() - iniX > this.threshold.get()) {
                    swipedRight.set(true);
                } else if (e.getSceneX() - iniX < -this.threshold.get()) {
                    swipedLeft.set(true);
                } else {
                    // reset without transition
                    translateTile(0);
                }
            }
            // reset 
            scrolling.set(false);
            sliding.set(false);
        });
        
        this.getChildren().addAll(backPane,tile);
    }
    
    private void translateTile(double posX) {
        tile.setTranslateX(posX);
    }
    
    /**
     * Reset position of tile with smooth transition
     */
    public void resetTilePosition() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), tile);
        transition.setInterpolator(Interpolator.EASE_OUT);
        transition.setFromX(tile.getTranslateX());
        transition.setToX(0);
        transition.playFromStart();
    }
    
    public BooleanProperty swipedLeftProperty() {
        return swipedLeft;
    }
    
    public BooleanProperty swipedRightProperty() {
        return swipedRight;
    }
    
    public DoubleProperty thresholdProperty() {
        return threshold;
    }
    
    public StringProperty textLeftProperty(){
        return textLeft;
    }
    
    public StringProperty textRightProperty(){
        return textRight;
    }

    public BooleanProperty slidingProperty() {
        return sliding;
    }
    
    public BooleanProperty scrollingProperty() {
        return scrolling;
    }
    
    public BooleanProperty allowedProperty() {
        return allowed;
    }
}
