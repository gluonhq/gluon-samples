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
package com.gluonhq.samples.fiftystates;

import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.samples.fiftystates.model.USState;
import com.gluonhq.samples.fiftystates.model.USStates;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class USStateCell extends CharmListCell<USState> {

    private final ListTile tile;
    private final ImageView imageView;

    public USStateCell() {
        this.tile = new ListTile();
        imageView = new ImageView();
        imageView.setFitHeight(15);
        imageView.setFitWidth(25);
        tile.setPrimaryGraphic(imageView);
        setText(null);
    }

    @Override
    public void updateItem(USState item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            tile.textProperty().setAll(item.getName() + " (" + item.getAbbr() + ")",
                    "Capital: " + item.getCapital() +
                            ", Population (M): " + String.format("%.2f", item.getPopulation() / 1_000_000d),
                    "Area (km" + "\u00B2" + "): " + item.getArea() +
                            ", Density (pop/km" + "\u00B2" + "): " + String.format("%.1f", item.getDensity())
            );
            final Image image = USStates.getImage(item.getFlag());
            if (image != null) {
                imageView.setImage(image);
            }
            setGraphic(tile);
        } else {
            setGraphic(null);
        }
    }

}