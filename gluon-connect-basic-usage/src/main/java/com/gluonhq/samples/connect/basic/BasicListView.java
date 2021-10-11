/*
 * Copyright (c) 2016, 2021, Gluon
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
package com.gluonhq.samples.connect.basic;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.converter.InputStreamIterableInputConverter;
import com.gluonhq.connect.converter.JsonIterableInputConverter;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.InputStreamListDataReader;
import com.gluonhq.connect.provider.ListDataReader;
import com.gluonhq.connect.source.BasicInputDataSource;
import com.gluonhq.connect.source.InputDataSource;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class BasicListView extends View {

    public BasicListView() {

        // create a DataSource that loads data from a classpath resource
        InputDataSource dataSource = new BasicInputDataSource(Main.class.getResourceAsStream("/languages.json"));

        // create a Converter that converts a json array into a list
        InputStreamIterableInputConverter<Language> converter = new JsonIterableInputConverter<>(Language.class);

        // create a ListDataReader that will read the data from the DataSource and converts
        // it from json into a list of objects
        ListDataReader<Language> listDataReader = new InputStreamListDataReader<>(dataSource, converter);

        // retrieve a list from the DataProvider
        GluonObservableList<Language> programmingLanguages = DataProvider.retrieveList(listDataReader);

        // create a JavaFX ListView and populate it with the retrieved list
        ListView<Language> lvProgrammingLanguages = new ListView<>(programmingLanguages);
        lvProgrammingLanguages.setCellFactory(lv -> new ListCell<Language>() {
            @Override
            protected void updateItem(Language item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    setText(item.getName() + ": " + item.getRatings() + "%");
                } else {
                    setText(null);
                }
            }
        });

        setCenter(lvProgrammingLanguages);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> getAppManager().getDrawer().open()));
        appBar.setTitleText("List Viewer");
    }
    
}
