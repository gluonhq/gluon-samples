/*
 * Copyright (c) 2021, Gluon
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
package com.gluonhq;

import com.gluonhq.attach.position.Position;
import com.gluonhq.attach.position.PositionService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.attach.util.Services;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloMaps extends Application {

    private static final int DEFAULT_ZOOM = 3;
    private static final double DEFAULT_LAT = 50.0;
    private static final double DEFAULT_LONG = 4.0;

    private MapPoint mapPoint;

    @Override
    public void start(Stage stage) {
        MapView view = new MapView();
        view.addLayer(positionLayer());
        view.setZoom(DEFAULT_ZOOM);
        Scene scene;
        if (Platform.isDesktop()) {
            scene = new Scene(view, 600, 700);
            stage.setTitle("Gluon Maps Demo");
        } else {
            BorderPane bp = new BorderPane();
            bp.setCenter(view);
            final Label label = new Label("Gluon Maps Demo");
            label.setAlignment(Pos.CENTER);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setStyle("-fx-background-color: dimgrey; -fx-text-fill: white;");
            bp.setTop(label);
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            scene = new Scene(bp, bounds.getWidth(), bounds.getHeight());
        }
        stage.setScene(scene);
        stage.show();

        view.flyTo(1., mapPoint, 2.);
    }

    private MapLayer positionLayer() {
        return Services.get(PositionService.class)
                .map(positionService -> {
                    positionService.start();

                    ReadOnlyObjectProperty<Position> positionProperty = positionService.positionProperty();
                    Position position = positionProperty.get();
                    if (position == null) {
                        position = new Position(DEFAULT_LAT, DEFAULT_LONG);
                    }
                    mapPoint = new MapPoint(position.getLatitude(), position.getLongitude());

                    PoiLayer answer = new PoiLayer();
                    answer.addPoint(mapPoint, new Circle(7, Color.RED));

                    positionProperty.addListener(e -> {
                        Position pos = positionProperty.get();
                        mapPoint.update(pos.getLatitude(), pos.getLongitude());
                    });
                    return answer;
                })
                .orElseGet(() -> {
                    PoiLayer answer = new PoiLayer();
                    mapPoint = new MapPoint(DEFAULT_LAT, DEFAULT_LONG);
                    answer.addPoint(mapPoint, new Circle(7, Color.RED));
                    return answer;
                });
    }

    public static void main(String[] args) {
        launch(args);
    }
}