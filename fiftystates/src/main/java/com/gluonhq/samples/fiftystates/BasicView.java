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

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.samples.fiftystates.model.Density;
import com.gluonhq.samples.fiftystates.model.Density.DENSITY;
import com.gluonhq.samples.fiftystates.model.USState;
import com.gluonhq.samples.fiftystates.model.USStates;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class BasicView extends View {

    private final FilteredList<USState> filteredList;
    private final CharmListView<USState, DENSITY> charmListView;
    private boolean ascending = true;

    public BasicView() {

        filteredList = new FilteredList<>(USStates.statesList, getStatePredicate(null));
        charmListView = new CharmListView<>(filteredList);
        charmListView.setCellFactory(p -> new USStateCell());
        charmListView.setHeadersFunction(Density::getDensity);
        charmListView.setHeaderCellFactory(p -> new CharmListCell<>() {

            private final ListTile tile = new ListTile();

            {
                Avatar avatar = new Avatar(16, USStates.getUSFlag());
                tile.setPrimaryGraphic(avatar);
                setText(null);
            }

            @Override
            public void updateItem(USState item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    tile.textProperty().setAll("Density", charmListView.toString(item));
                    setGraphic(tile);
                } else {
                    setGraphic(null);
                }
            }

        });
        charmListView.setConverter(new StringConverter<>() {

            @Override
            public String toString(DENSITY d) {
                return "From " + ((int) d.getIni()) + " up to " + ((int) d.getEnd()) + " pop/km" + "\u00B2";
            }

            @Override
            public DENSITY fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        setCenter(charmListView);

    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.STAR.button());
        appBar.setTitleText("50 States");

        Button sort = MaterialDesignIcon.SORT.button(e -> {
            if (ascending) {
                charmListView.setHeaderComparator(Enum::compareTo);
                charmListView.setComparator(Comparator.comparingDouble(USState::getDensity));
                ascending = false;
            } else {
                charmListView.setHeaderComparator(Comparator.reverseOrder());
                charmListView.setComparator((s1, s2) -> Double.compare(s2.getDensity(), s1.getDensity()));
                ascending = true;
            }
        });
        appBar.getActionItems().add(sort);
        appBar.getMenuItems().setAll(buildFilterMenu());

    }

    private Predicate<USState> getStatePredicate(Double population) {
        return state -> population == null || state.getPopulation() >= population * 1_000_000;
    }

    private List<MenuItem> buildFilterMenu() {
        final List<MenuItem> menu = new ArrayList<>();

        EventHandler<ActionEvent> menuActionHandler = e -> {
            MenuItem item = (MenuItem) e.getSource();
            Double population = (Double) item.getUserData();
            filteredList.setPredicate(getStatePredicate(population));
        };

        ToggleGroup toggleGroup = new ToggleGroup();

        RadioMenuItem allStates = new RadioMenuItem("All States");
        allStates.setOnAction(menuActionHandler);
        allStates.setSelected(true);
        menu.add(allStates);
        toggleGroup.getToggles().add(allStates);

        List<Double> items = Arrays.asList(0.5, 1.0, 2.5, 5.0);
        for (Double d : items) {
            RadioMenuItem item = new RadioMenuItem("Population > " + d + "M");
            item.setUserData(d);
            item.setOnAction(menuActionHandler);
            menu.add(item);
            toggleGroup.getToggles().add(item);
        }

        return menu;
    }
}
