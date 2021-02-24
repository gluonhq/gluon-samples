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
package com.gluonhq.samples.alarm.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Event {

    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty text = new SimpleStringProperty();
    private final LongProperty creationDate = new SimpleLongProperty();
    private final LongProperty eventDate = new SimpleLongProperty();
    private final BooleanProperty delivered = new SimpleBooleanProperty();

    public Event() {
    }

    public Event(String id, String title, String text, ZonedDateTime creationDate, ZonedDateTime eventDate) {
        this.id.set(id);
        this.title.set(title);
        this.text.set(text);
        this.creationDate.set(creationDate.toInstant().toEpochMilli());
        this.eventDate.set(eventDate.toInstant().toEpochMilli());
    }

    public final String getId() { return id.get(); }
    public final void setId(String id) { this.id.set(id); }
    public final StringProperty idProperty() { return this.id; }
    
    public final String getTitle() { return title.get(); }
    public final void setTitle(String title) { this.title.set(title); }
    public final StringProperty titleProperty() { return this.title; }

    public final String getText() { return text.get(); }
    public final void setText(String text) { this.text.set(text); }
    public final StringProperty textProperty() { return this.text; }

    public final Long getCreationDate() { return creationDate.get(); }
    public final void setCreationDate(long creationDate) { this.creationDate.set(creationDate); }
    public final LongProperty creationDateProperty() { return creationDate; }

    public final Long getEventDate() { return eventDate.get(); }
    public final void setEventDate(long eventDate) { this.eventDate.set(eventDate); }
    public final LongProperty eventDateProperty() { return eventDate; }

    public final Boolean isDelivered() { return delivered.get(); }
    public final void setDelivered(boolean delivered) { this.delivered.set(delivered); }
    public final BooleanProperty deliveredProperty() { return delivered; }

    public final ZonedDateTime getCreationZonedDateTime() {
        return timeToZonedDateTime(getCreationDate());
    }

    public final ZonedDateTime getEventZonedDateTime() {
        return timeToZonedDateTime(getEventDate());
    }

    private static ZonedDateTime timeToZonedDateTime(long time) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        return " Title " + title.get() + " for id " + id.get();
    }
}