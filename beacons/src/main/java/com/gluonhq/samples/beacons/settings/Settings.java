/*
 * Copyright (c) 2016, 2020, Gluon
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
package com.gluonhq.samples.beacons.settings;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Settings {

	public static final String UUID = "74278BDA-B644-4520-8F0C-720EAF059935";
	public static final String MAJOR = "5";
	public static final String MINOR = "1";
	public static final String ID = "GluonBeacon";

	private final StringProperty uuid = new SimpleStringProperty(UUID);
	public final StringProperty uuidProperty() {
		return this.uuid;
	}
	public final String getUuid() {
		return uuid.get();
	}
	public final void setUuid(final String uuid) {
		this.uuid.set(uuid);
	}

	private final StringProperty major = new SimpleStringProperty(MAJOR);
	public final StringProperty majorProperty() {
		return this.major;
	}
	public final String getMajor() {
		return major.get();
	}
	public final void setMajor(final String major) {
		this.major.set(major);
	}

	private final StringProperty minor = new SimpleStringProperty(MINOR);
	public final StringProperty minorProperty() {
		return this.minor;
	}
	public final String getMinor() {
		return minor.get();
	}
	public final void setMinor(final String minor) {
		this.minor.set(minor);
	}

	private final StringProperty id = new SimpleStringProperty(ID);
	public final StringProperty idProperty() {
		return this.id;
	}
	public final String getId() {
		return id.get();
	}
	public final void setId(final String id) {
		this.id.set(id);
	}
}
