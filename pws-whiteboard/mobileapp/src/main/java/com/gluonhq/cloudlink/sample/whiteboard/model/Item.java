package com.gluonhq.cloudlink.sample.whiteboard.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Item {

    private StringProperty title = new SimpleStringProperty();
    private long creationDate;

    public Item() {
        creationDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public LocalDateTime getFormattedCreationDate() {
        return LocalDateTime.ofEpochSecond(creationDate, 0, ZoneOffset.UTC);
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
