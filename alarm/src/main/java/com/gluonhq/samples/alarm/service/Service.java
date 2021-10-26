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
package com.gluonhq.samples.alarm.service;

import com.gluonhq.attach.localnotifications.LocalNotificationsService;
import com.gluonhq.attach.localnotifications.Notification;
import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.samples.alarm.model.Event;
import com.gluonhq.samples.alarm.views.AppViewManager;
import com.gluonhq.samples.alarm.views.EventsPresenter;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class Service {

    private static final String EVENTS = "events-v1";

    private final ListProperty<Event> events = new SimpleListProperty<>(FXCollections.observableArrayList());

    private DataClient dataClient;

    @PostConstruct
    public void postConstruct() {
        dataClient = DataClientBuilder.create()
                .operationMode(OperationMode.LOCAL_ONLY)
                .build();
    }

    public void retrieveEvents(boolean startup) {
        GluonObservableList<Event> gluonEvents = DataProvider.retrieveList(
                dataClient.createListDataReader(EVENTS, Event.class,
                        SyncFlag.LIST_WRITE_THROUGH, SyncFlag.LIST_READ_THROUGH,
                        SyncFlag.OBJECT_WRITE_THROUGH, SyncFlag.OBJECT_READ_THROUGH));

        gluonEvents.setOnSucceeded(e -> {
            events.set(gluonEvents);
            if (startup) {
                preloadNotifications();
            }
        });
    }

    public void addEvent(Event event) {
        events.get().add(event);
        Notification notification = createNotification(event, true);
        LocalNotificationsService.create().ifPresent(ln ->
                ln.getNotifications().add(notification));
    }

    public void removeEvent(Event event) {
        LocalNotificationsService.create().ifPresent(ln -> {
            ln.getNotifications().stream()
                    .filter(n -> n.getId().equals(event.getId()))
                    .findFirst()
                    .ifPresent(n -> ln.getNotifications().remove(n));
        });
        events.get().remove(event);
    }

    public ListProperty<Event> notesProperty() {
        return events;
    }

    /**
     *  Creates a notification from a given event
     * @param event the event containing information to create a notification
     * @param schedule if true, the notification will be send to the device to
     *                 be scheduled. If false, it won't be send.
     * @return a notification
     */
    public Notification createNotification(Event event, boolean schedule) {
        return new Notification(event.getId(), event.getTitle(), event.getText(),
                schedule ? Service.class.getResourceAsStream("baseline_alarm_add_black_18dp.png") : null,
                schedule ? event.getEventZonedDateTime() : null,
                () -> {
                    events.stream()
                            .filter(ev -> ev.getId().equals(event.getId()))
                            .findFirst()
                            .ifPresentOrElse(ev -> ev.setDelivered(true),
                                    () -> System.out.println("Error: Id " + event.getId() + " not found"));
                    AppViewManager.EVENTS_VIEW.switchView().ifPresent(p ->
                            Platform.runLater(() -> ((EventsPresenter) p).selectEvent(event.getId())));
                });
    }

    /**
     * Add pending notifications (without scheduling them) to
     * the notifications list.
     *
     * The list gets lost every time the app is closed, so this
     * has to be recreated on startup, but without rescheduling the
     * same events.
     */
    private void preloadNotifications() {
        List<Notification> notifications = events.stream()
                .filter(event -> !event.isDelivered())
                .map(event -> createNotification(event, false))
                .collect(Collectors.toList());

        LocalNotificationsService.create().ifPresent(ln ->
                            ln.getNotifications().addAll(notifications));
    }

}
