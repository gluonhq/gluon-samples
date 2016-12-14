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
package com.gluonhq.notesapp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Settings {
    
    public static enum SORTING {
        DATE(0), 
        TITLE(1), 
        CONTENT(2);
        
        final int id;
        
        SORTING(int id) {
            this.id = id;
        }
    
        public int getId() {
            return id;
        }
        
        public static SORTING byId(int id) {
            for (SORTING s : SORTING.values()) {
                if (s.getId() == id) {
                    return s;
                }
            }
            return DATE;
        }
    }
    
    // showDate
    private final BooleanProperty showDate = new SimpleBooleanProperty(this, "showDate", true);
    
    public final BooleanProperty showDateProperty() {
       return showDate;
    }
    
    public final boolean isShowDate() {
       return showDate.get();
    }
    
    public final void setShowDate(boolean value) {
        showDate.set(value);
    }
    
    // ascending
    private final BooleanProperty ascending = new SimpleBooleanProperty(this, "ascending", true);
    
    public final BooleanProperty ascendingProperty() {
       return ascending;
    }
    
    public final boolean isAscending() {
       return ascending.get();
    }
    
    public final void setAscending(boolean value) {
        ascending.set(value);
    }
    
    // sortingProperty (enum is not persisted)
    private final ObjectProperty<SORTING> sorting = new SimpleObjectProperty<SORTING>(this, "sorting", SORTING.DATE) {
        @Override
        protected void invalidated() {
            sortingId.set(get().getId());
        }
        
    };
    
    public final ObjectProperty<SORTING> sortingProperty() {
       return sorting;
    }
    
    public final SORTING getSorting() {
       return sorting.get();
    }
    
    public final void setSorting(SORTING value) {
        sorting.set(value);
    }
    
    // sortingIdProperty, just for persistence
    private final IntegerProperty sortingId = new SimpleIntegerProperty(this, "sortingId", SORTING.DATE.getId()) {
        @Override
        protected void invalidated() {
            setSorting(SORTING.byId(get()));
        }

    };
    
    // fontSize
    private final IntegerProperty fontSize = new SimpleIntegerProperty(this, "fontSize", 10);
    
    public final IntegerProperty fontSizeProperty() {
       return fontSize;
    }
    
    public final int getFontSize() {
       return fontSize.get();
    }
    
    public final void setFontSize(int value) {
        fontSize.set(value);
    }
    
}
