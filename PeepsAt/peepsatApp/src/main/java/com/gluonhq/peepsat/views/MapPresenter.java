/*
 * Copyright (c) 2018, Gluon
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
package com.gluonhq.peepsat.views;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.Position;
import com.gluonhq.charm.down.plugins.PositionService;
import com.gluonhq.charm.down.plugins.SettingsService;
import com.gluonhq.charm.glisten.afterburner.GluonPresenter;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunctionObject;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.gluonhq.peepsat.Model;
import com.gluonhq.peepsat.PeepsAt;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Random;
import java.util.ResourceBundle;

public class MapPresenter extends GluonPresenter<PeepsAt> {

    private static final double DEFAULT_ZOOM = 15.0;
    static final String DEFAULT_LATITUDE = "50.4";
    static final String DEFAULT_LONGITUDE = "4.1";
    @FXML
    private View map;

    @FXML
    private MapView mapView;
    
    @FXML
    private ResourceBundle resources;
    
    private MapPoint myLocationPoint;

    private long lastStoredDate = 0L;
    
    public void initialize() {
        try {
            setupLocationService();
        } catch (Throwable t) {
            System.err.println("Error setting up locationservice");
            t.printStackTrace();
        }
        FloatingActionButton fabAddFriend = new FloatingActionButton();
        fabAddFriend.setText(MaterialDesignIcon.PERSON_ADD.text);
        fabAddFriend.setOnAction(e -> createDialog().showAndWait());
        fabAddFriend.showOn(map);
        
        map.setOnShowing(e -> {
            getApp().getAppBar().setVisible(false);

            // FixME: The following is a hack to reset zoom value 
            mapView.setZoom(10.0);
            mapView.setZoom(DEFAULT_ZOOM);
            setupMyLocation();
        });
    }

    private void setupMyLocation() {
        SettingsService settingsService = Services.get(SettingsService.class).get();
        String myName = Model.username;
        String latitude = settingsService.retrieve("lat");
        String longitude = settingsService.retrieve("long");
        if (latitude == null) latitude = DEFAULT_LATITUDE;
        if (longitude == null) longitude = DEFAULT_LONGITUDE;
        Position position = new Position(Double.valueOf(latitude), Double.valueOf(longitude));
        myLocationPoint = new MapPoint(position.getLatitude(), position.getLongitude());
        mapView.setCenter(myLocationPoint);
        addMarker(myName, myLocationPoint);
    }
    
    private void addMarker(String username, MapPoint mapPoint) {
        MapLayer venueMarker = createVenueMarker(username, mapPoint);
        mapView.addLayer(venueMarker);
    }

    private MapLayer createVenueMarker(String username, MapPoint venue) {
        PoiLayer answer = new PoiLayer();
        answer.getStyleClass().add("poi-layer");
        Node marker = MaterialDesignIcon.ROOM.graphic();
        marker.getStyleClass().add("marker");
        Group box = new Group(marker);
        box.getStyleClass().add("marker-container");
        final Text userNameText = new Text(username);
        userNameText.getStyleClass().add("text");
        VBox container = new VBox(userNameText, box);
        container.getStyleClass().add("container");
        container.getStyleClass().add(getRandomStyleClass());
        answer.addPoint(venue, new Group(container));
        return answer;
    }

    private Dialog createDialog() {
        Dialog dialog = new Dialog();
        final TextField textField = new TextField();
        final VBox content = new VBox(10, new Label("Enter your friend's username"), textField);
        dialog.setContent(content);

        Button find = new Button("Find");
        find.setDisable(true);
        textField.textProperty().addListener((obs, ov, nv) -> find.setDisable(nv == null || nv.isEmpty()));
        find.setOnAction(e -> {
            find.setDisable(true);
            final String text = textField.getText();
            textField.setErrorValidator(s -> "");
            final GluonObservableObject<String> latlon = addFriend(text);
            latlon.stateProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable e) {
                    System.out.println("addfriend changed to " + latlon.getState());
                    if (latlon.getState().equals(ConnectState.FAILED)) {
                        if (latlon.getException() != null) {
                            latlon.getException().printStackTrace();
                        }
                        latlon.stateProperty().removeListener(this);
                        // hack to show error message below text
                        textField.setErrorValidator(s -> "Couldn't find User");
                        final String text = textField.getText();
                        textField.setText("");
                        textField.setText(text);
                        find.setDisable(false);
                    }
                    if (latlon.getState().equals(ConnectState.SUCCEEDED)) {
                        latlon.stateProperty().removeListener(this);
                        find.setDisable(false);
                        dialog.hide();
                        showFriend(text, latlon.get());
                    }
                }
            });
            if (latlon.getState().equals(ConnectState.SUCCEEDED)) {
                dialog.hide();
                showFriend(text, latlon.get());
            }
        });
        dialog.getButtons().setAll(find);
        return dialog;
    }

    private static final String[] styleClass = {"red", "blue", "green", "yellow"};
    private String getRandomStyleClass() {
        Random rnd = new Random();
        int i = rnd.nextInt(styleClass.length);
        return styleClass[i];
    }

    private GluonObservableObject<String> addFriend(String f) {
        RemoteFunctionObject function = RemoteFunctionBuilder.create("getFriendLocation")
                .param("name", f)
                .object();
        return function.call(String.class);
    }

    private Position parseLatLon(String l) {
        try {
            final String[] split = l.split(",");
            double lat = Double.valueOf(split[1]);
            double lon = Double.valueOf(split[2]);
            return new Position(lat, lon);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Position(0., 0.);
        }
    }

    private void showFriend(String username, String position) {
        Position fp = parseLatLon(position);
        MapPoint mp = new MapPoint(fp.getLatitude(), fp.getLongitude());
        mapView.setCenter(mp);
        addMarker(username, mp);
    }
    
    private void setupLocationService() {
        Services.get(PositionService.class).ifPresent(ps -> {
            ps.positionProperty().addListener(e -> {
                Position myPosition = ps.getPosition();
                if (myLocationPoint != null) {
                    myLocationPoint.update(myPosition.getLatitude(), myPosition.getLongitude());
                    mapView.setCenter(myPosition.getLatitude(), myPosition.getLongitude());
                    if (System.currentTimeMillis() - lastStoredDate > 10000) {
                        lastStoredDate = System.currentTimeMillis();
                        setPosition(myPosition);
                    }
                }
            });
        });
    }
   
    /**
     * Stores the position in the settings, and upload it via a Remote Function
     * @param newValue 
     */
    private void setPosition(Position newValue) {
        SettingsService settingsService = Services.get(SettingsService.class).get();
        settingsService.store("lat", String.valueOf(newValue.getLatitude()));
        settingsService.store("long", String.valueOf(newValue.getLongitude()));
        uploadMyLocation(newValue);
    }

    private void uploadMyLocation(Position p) {
        RemoteFunctionObject function
                = RemoteFunctionBuilder.create("SaveLocation")
                .param("name", Model.username)
                .param("lat", String.valueOf(p.getLatitude()))
                .param("long", String.valueOf(p.getLongitude()))
                .object();
        GluonObservableObject<String> res = function.call(String.class);
        res.stateProperty().addListener((o, t0, t1) -> {
            System.out.println("upload my location state changed to " + t1);
            if (t1.equals(ConnectState.FAILED)) {
                if (res.getException() != null) {
                    res.getException().printStackTrace();
                }
            } else if (t1.equals(ConnectState.SUCCEEDED)) {
                System.out.println("succeeded!");
            }
        });
    }
    
    class PoiLayer extends MapLayer {

        private final ObservableList<Pair<MapPoint, Node>> points = FXCollections.observableArrayList();

        public PoiLayer() {
        }

        public void addPoint(MapPoint p, Node icon) {
            points.add(new Pair(p, icon));
            icon.setVisible(false);
            this.getChildren().add(icon);
            // required to layout first the node and be able to find its 
            // bounds
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(f -> {
                markDirty();
                icon.setVisible(true);
            });
            pause.play();
        }

        @Override
        protected void layoutLayer() {
            for (Pair<MapPoint, Node> candidate : points) {
                MapPoint point = candidate.getKey();
                Node icon = candidate.getValue();
                Bounds bounds = icon.getBoundsInParent();
                Point2D mapPoint = baseMap.getMapPoint(point.getLatitude(), point.getLongitude());
                // translate icon so marker base point is at the center
                icon.setTranslateX(mapPoint.getX() - bounds.getWidth() / 2);
                icon.setTranslateY(mapPoint.getY() - bounds.getHeight() / 2 - 10);
            }
        }

    }
}
