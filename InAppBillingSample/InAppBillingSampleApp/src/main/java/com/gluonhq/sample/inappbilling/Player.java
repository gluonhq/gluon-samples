/**
 * Copyright (c) 2018 Gluon
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
package com.gluonhq.sample.inappbilling;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.SettingsService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Random;

public class Player {

    private static final String SETTING_HEALTH = "health";
    private static final String SETTING_OWNED_HEALTH_POTIONS = "owned_health_potions";

    public static final int MAX_HEALTH = 10;

    private static final Random RND = new Random();

    private final SettingsService settingsService;

    private final ReadOnlyIntegerWrapper health = new ReadOnlyIntegerWrapper(MAX_HEALTH);
    private final ReadOnlyIntegerWrapper ownedHealthPotions = new ReadOnlyIntegerWrapper(0);
    private final BooleanProperty woodenShieldOwned = new SimpleBooleanProperty(false);

    public Player() {
        settingsService = Services.get(SettingsService.class).orElseThrow(() -> new RuntimeException("SettingsService must be added to list of charm down plugins"));

        health.set(getOrDefaultInteger(SETTING_HEALTH, 10));
        ownedHealthPotions.set(getOrDefaultInteger(SETTING_OWNED_HEALTH_POTIONS, 0));
    }

    public ReadOnlyIntegerProperty healthProperty() {
        return health.getReadOnlyProperty();
    }

    public ReadOnlyIntegerProperty ownedHealthPotionsProperty() {
        return ownedHealthPotions.getReadOnlyProperty();
    }

    public void dealDamage() {
        int damage = RND.nextInt(woodenShieldOwned.get() ? 3 : 4);
        int newHealth = health.get() - damage;
        health.set(newHealth >= 0 ? newHealth : 0);

        this.settingsService.store(SETTING_HEALTH, String.valueOf(this.health.get()));
    }

    public void consumeHealthPotion() {
        if (this.ownedHealthPotions.get() > 0) {
            this.health.set(MAX_HEALTH);
            this.ownedHealthPotions.set(this.ownedHealthPotions.get() - 1);

            this.settingsService.store(SETTING_HEALTH, String.valueOf(this.health.get()));
            this.settingsService.store(SETTING_OWNED_HEALTH_POTIONS, String.valueOf(this.ownedHealthPotions.get()));
        }
    }

    public void boughtHealthPotion() {
        this.ownedHealthPotions.set(this.ownedHealthPotions.get() + 1);

        this.settingsService.store(SETTING_OWNED_HEALTH_POTIONS, String.valueOf(this.ownedHealthPotions.get()));
    }

    public boolean isWoodenShieldOwned() {
        return woodenShieldOwned.get();
    }

    public BooleanProperty woodenShieldOwnedProperty() {
        return woodenShieldOwned;
    }

    public void setWoodenShieldOwned(boolean woodenShieldOwned) {
        this.woodenShieldOwned.set(woodenShieldOwned);
    }

    private int getOrDefaultInteger(String setting, int defaultValue) {
        String value = settingsService.retrieve(setting);
        if (value == null) {
            settingsService.store(setting, String.valueOf(defaultValue));
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }
}
