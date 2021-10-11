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
package com.gluonhq.samples.alarm.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ExpansionPanelContainer;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.control.TimePicker;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.alarm.model.Event;
import com.gluonhq.samples.alarm.service.Service;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AlarmPresenter {

    @FXML private View alarm;
    @FXML private ExpansionPanelContainer expansionContainer;
    @FXML private Label alarmAtLabel;
    @FXML private ToggleButton toggleAt;
    @FXML private TextField titleAtText;
    @FXML private TextField messageAtText;
    @FXML private Button timeAtButton;
    @FXML private Label alarmInLabel;
    @FXML private ToggleButton toggleIn;
    @FXML private TextField titleInText;
    @FXML private TextField messageInText;
    @FXML private TextField hourInText;
    @FXML private TextField minInText;
    @FXML private TextField secInText;
    @FXML private Button scheduleButton;

    @FXML private ResourceBundle resources;

    @Inject private Service service;

    private LocalTime localTime;

    public void initialize() {
        timeAtButton.setOnAction(e -> {
            TimePicker timePicker = new TimePicker();
            timePicker.setTime(LocalTime.now());
            timePicker.showAndWait().ifPresent(t -> {
                localTime = t;
                alarmAtLabel.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime));
            });
        });

        alarm.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = AppManager.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("appbar.title"));
                appBar.getActionItems().add(MaterialDesignIcon.LIST.button(e ->
                        AppViewManager.EVENTS_VIEW.switchView()));
            }
        });

        expansionContainer.getItems().forEach(panel ->  {
            panel.expandedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    expansionContainer.getItems().stream()
                            .filter(otherPanel -> !otherPanel.equals(panel))
                            .forEach(otherPanel -> otherPanel.setExpanded(false));
                }
            });
        });

        hourInText.setTextFormatter(new TextFormatter<>(new RangeStringConverter(0, 23), 0));
        minInText.setTextFormatter(new TextFormatter<>(new RangeStringConverter(0, 59), 0));
        secInText.setTextFormatter(new TextFormatter<>(new RangeStringConverter(0, 59), 0));
        alarmInLabel.textProperty().bind(Bindings.format("%s:%s:%s", hourInText.textProperty(), minInText.textProperty(), secInText.textProperty()));
        service.retrieveEvents(false);
    }

    @FXML
    void scheduleButtonAction() {
        Event event;
        String id = "" + System.currentTimeMillis();
        if (toggleAt.isSelected() && localTime != null) {
            ZonedDateTime dateTime = ZonedDateTime.of(LocalDate.now(), localTime, ZoneId.systemDefault());
            event = new Event(id, titleAtText.getText(), messageAtText.getText(), LocalDateTime.now().atZone(ZoneId.systemDefault()), dateTime);
        } else if (toggleIn.isSelected()) {
            ZonedDateTime dateTime = ZonedDateTime.of(LocalDate.now(),
                        LocalTime.now()
                                .plusHours(Long.parseLong(hourInText.getText()))
                                .plusMinutes(Long.parseLong(minInText.getText()))
                                .plusSeconds(Long.parseLong(secInText.getText())),
                        ZoneId.systemDefault());
            event = new Event(id, titleInText.getText(), messageInText.getText(), LocalDateTime.now().atZone(ZoneId.systemDefault()), dateTime);
        } else {
            return;
        }
        service.addEvent(event);
        Toast toast = new Toast(resources.getString("alarm.set.text"));
        toast.show();
    }

    private static class RangeStringConverter extends StringConverter<Integer> {

        private final int min;
        private final int max;

        public RangeStringConverter(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString(Integer object) {
            return String.format("%02d", object);
        }

        @Override
        public Integer fromString(String string) {
            int i = Integer.parseInt(string);
            if (i > max || i < min) {
                throw new IllegalArgumentException();
            }
            return i;
        }

    }
}
