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
package com.gluonhq.samples.cloudlink.connector.rest.service;

import com.gluonhq.samples.cloudlink.connector.rest.model.Settings;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * An EJB Session bean that works as a service class for managing the Settings object in the configured JPA database.
 */
@Stateless
public class SettingsService {

    private static final String SETTINGS_ID = "SETTINGS_IDENTIFIER";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find the settings object.
     *
     * @return the default Settings object
     */
    public Settings findSettings() {
        Settings settings = entityManager.find(Settings.class, SETTINGS_ID);
        if (settings == null) {
            settings = new Settings();
            settings.setId(SETTINGS_ID);
            settings.setShowDate(true);
            settings.setAscending(true);
            settings.setSortingId(0);
            settings.setFontSize(10);
            entityManager.persist(settings);
        }
        return settings;
    }

    /**
     * Update the Settings object with the provided parameters.
     *
     * @param showDate whether to show the data or not
     * @param ascending whether to sort ascending or descending
     * @param sortingId the id of the sort method: 0 = DATE, 1 =TITLE or 2 = CONTENT
     * @param fontSize the font size to use for displaying the notes
     * @return the merged Settings
     */
    public Settings updateSettings(boolean showDate, boolean ascending, int sortingId, int fontSize) {
        Settings settings = findSettings();
        settings.setShowDate(showDate);
        settings.setAscending(ascending);
        settings.setSortingId(sortingId);
        settings.setFontSize(fontSize);
        return entityManager.merge(settings);
    }
}
