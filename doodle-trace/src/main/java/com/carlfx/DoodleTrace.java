/*
 * Copyright (c) 2016, 2020 Gluon
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
package com.carlfx;

import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 * Doodle Trace is a Gluon based cross platform
 * application allowing you to doodle and
 * watch a ball trace your doodle.
 * Created by cpdea on 3/12/16.
 */
public class DoodleTrace extends View {
    /** Ball that traces */
    private Circle ball;

    private Path drawPath;
    private Path tracePath;

    /** Starting point MoveTo path Element */
    private Point2D anchorPt = new Point2D(100,100);

    /** The tracing animation timeline */
    private Timeline tracerTimeline;
    private int totalPointsTraced;

    /** Flag to prevent user from drawing while animating */
    private volatile boolean animating = false;

    /** The amount of time to animate the line tracing */
    private Duration speed = Duration.millis(4000);

    /** Functional interface for mouse and touch events for drawing (doodling) */
    interface PathAction {
        void path(double x, double y);
    }

    /**
     * Start initial point (anchor point).
     */
    PathAction start = (x, y) -> {
        // start point in path
        anchorPt = new Point2D(x, y);

        if (!animating) {
            drawPath.setVisible(true);
            drawPath.getElements()
                    .add(new MoveTo(anchorPt.getX(),
                                    anchorPt.getY()));
        }
    };

    /**
     * Draw connected lines as mouse or touch points are applied.
     */
    PathAction draw = (x, y) -> {
        if (!animating) drawPath.getElements()
                                .add(new LineTo(x, y));
    };

    /**
     * When the mouse button is released.
     */
    PathAction end = (x, y) -> {
        // Does nothing
    };


    /** Animate the ball on the trace path per path element. */
    EventHandler<ActionEvent> animAction = actionEvent -> {

        // obtain numPts
        int numPts = drawPath.getElements().size();
        if (numPts == 0) return;

        // Hide the drawn path
        drawPath.setVisible(false);

        // Clear the trace path
        tracePath.getElements().clear();

        // Calculate one key frame duration
        Duration oneFrameAmt = Duration.millis(speed.toMillis() / numPts);

        totalPointsTraced = 0;
        animating = true;

        // Each cycle take a path element to be added to trace path.
        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, traceAction -> {
            if (drawPath.getElements().size()>0 &&
                    totalPointsTraced < numPts-1) {

                PathElement pe = drawPath.getElements()
                        .get(totalPointsTraced);

                double x = 0;
                double y = 0;
                if (pe instanceof MoveTo) {
                    MoveTo moveTo = (MoveTo) pe;
                    x = moveTo.getX();
                    y = moveTo.getY();
                } else if (pe instanceof LineTo) {
                    LineTo lineTo = (LineTo) pe;
                    x = lineTo.getX();
                    y = lineTo.getY();
                }
                ball.setCenterX(x);
                ball.setCenterY(y);
                tracePath.getElements()
                        .add(drawPath.getElements()
                                .get(totalPointsTraced));
            }
            totalPointsTraced++;
        });

        // Kick off animation based on the number of points
        if (drawPath.getElements().size() > 1) {
            tracerTimeline.getKeyFrames().clear();
            tracerTimeline.getKeyFrames().add(oneFrame);
            tracerTimeline.setCycleCount(numPts);
            tracerTimeline.playFromStart();
        }
    };

    /** Action that clears both draw path and trace path */
    EventHandler<ActionEvent> clearAction = actionEvent -> {
        drawPath.getElements().clear();
        tracePath.getElements().clear();
    };

    /**
     * Constructor taking the application's name.
     */
    public DoodleTrace() {

        // Create the drawing surface
        Pane drawSurface = new Pane();
        setCenter(drawSurface);

        // Initialize draw path
        drawPath = new Path();
        drawPath.setStrokeWidth(3);
        drawPath.setStroke(Color.BLACK);
        drawSurface.getChildren().add(drawPath);

        // Initialize trace path
        tracePath = new Path();
        tracePath.setStrokeWidth(3);
        tracePath.setStroke(Color.BLACK);
        drawSurface.getChildren().add(tracePath);

        // Ball tracer
        RadialGradient gradient1 = new RadialGradient(
                0,
                0,
                .5,
                .5,
                .55,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.BLACK));

        // Create a ball
        ball = new Circle(100, 100, 20, gradient1);

        // Add ball
        drawSurface.getChildren().add(ball);

        // Animation responsible for tracing the doodle
        tracerTimeline = new Timeline();

        // Flag to prevent user from doodling during animation
        tracerTimeline.setOnFinished(ae3 -> animating = false);

        if (Platform.isDesktop()) {
            applyMouseInput(drawSurface);
        } else if (Platform.isAndroid() || Platform.isIOS()) {
            applyTouchInput(drawSurface);
        }
    }

    /**
     * Apply mouse listener events for desktop platforms.
     * @param drawSurface The drawing surface to receive mouse input.
     */
    private void applyMouseInput(Pane drawSurface) {
        // starting initial path
        drawSurface.setOnMousePressed(mouseEvent ->
            start.path(mouseEvent.getX(), mouseEvent.getY()));

        // dragging creates lineTos added to the path
        drawSurface.setOnMouseDragged(mouseEvent ->
            draw.path(mouseEvent.getX(), mouseEvent.getY()));

        // end the path when mouse released event
        drawSurface.setOnMouseReleased(mouseEvent ->
            end.path(mouseEvent.getX(), mouseEvent.getY()));
    }

    /**
     * Apply touch listener events for touch platforms.
     * @param drawSurface The drawing surface to receive touch input.
     */
    private void applyTouchInput(Pane drawSurface) {
        // starting initial path
        drawSurface.setOnTouchPressed(touchEvent ->
            start.path(touchEvent.getTouchPoint().getX(),
                    touchEvent.getTouchPoint().getY()));

        // dragging creates lineTos added to the path
        drawSurface.setOnTouchMoved(touchEvent ->
            draw.path(touchEvent.getTouchPoint().getX(),
                         touchEvent.getTouchPoint().getY()));

        // end the path when mouse released event
        drawSurface.setOnTouchReleased( touchEvent ->
            end.path(touchEvent.getTouchPoint().getX(),
                touchEvent.getTouchPoint().getY()));
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Doodle Trace");

        //===================
        // Floating buttons
        //===================
        // Play animation button (Bottom Center)
        FloatingActionButton animateButton = new FloatingActionButton(
                MaterialDesignIcon.PLAY_ARROW.text, animAction);
        animateButton.setFloatingActionButtonHandler(
                FloatingActionButton.BOTTOM_CENTER);
        animateButton.showOn(this);

        // Clear Button (Botton Right)
        FloatingActionButton clearButton = new FloatingActionButton(
                MaterialDesignIcon.REFRESH.text, clearAction);
        clearButton.setFloatingActionButtonHandler(
                FloatingActionButton.BOTTOM_RIGHT);
        clearButton.showOn(this);

        //===================
        // Menu Items
        //===================
        // Checkbox Menu item to show or hide floating button controls
        CheckMenuItem showControlsMenuItem = new CheckMenuItem("Show/Hide Controls");
        showControlsMenuItem.setSelected(true);
        showControlsMenuItem.selectedProperty().addListener((obv, ov, nv) -> {
            if (nv) {
                animateButton.show();
                clearButton.show();
            } else {
                animateButton.hide();
                clearButton.hide();
            }
        });

        // Menu item to animate
        MenuItem animateMenuItem = new MenuItem("Animate", MaterialDesignIcon.PLAY_ARROW.graphic());
        animateMenuItem.setOnAction(animAction);

        // Menu item to clear
        MenuItem clearMenuItem = new MenuItem("Clear", MaterialDesignIcon.REFRESH.graphic());
        clearMenuItem.setOnAction(clearAction);

        appBar.getMenuItems()
              .addAll(showControlsMenuItem, animateMenuItem, clearMenuItem);
    }
}
