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
package com.gluonhq.samples.connect.rest;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlElement;

public class Question {
    private final IntegerProperty questionId = new SimpleIntegerProperty();
    private final BooleanProperty answered = new SimpleBooleanProperty();
    private final IntegerProperty viewCount = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty link = new SimpleStringProperty();

    @XmlElement(name = "question_id")
    public int getQuestionId() {
        return questionId.get();
    }

    public IntegerProperty questionIdProperty() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId.set(questionId);
    }

    @XmlElement(name = "is_answered")
    public boolean getAnswered() {
        return answered.get();
    }

    public BooleanProperty answeredProperty() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered.set(answered);
    }

    @XmlElement(name = "view_count")
    public int getViewCount() {
        return viewCount.get();
    }

    public IntegerProperty viewCountProperty() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount.set(viewCount);
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

    public String getLink() {
        return link.get();
    }

    public StringProperty linkProperty() {
        return link;
    }

    public void setLink(String link) {
        this.link.set(link);
    }
}
