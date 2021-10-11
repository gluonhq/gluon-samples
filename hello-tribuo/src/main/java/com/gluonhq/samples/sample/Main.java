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
package com.gluonhq.samples.sample;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.tribuo.MutableDataset;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.dtree.CARTClassificationTrainer;
import org.tribuo.classification.evaluation.LabelEvaluation;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.common.tree.TreeModel;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.datasource.ListDataSource;
import org.tribuo.evaluation.TrainTestSplitter;

import java.net.URL;

import static com.gluonhq.charm.glisten.application.AppManager.HOME_VIEW;

public class Main extends Application {

    private final AppManager appManager = AppManager.initialize(this::postInit);

    private final Series<String, Number> tpSeries = new Series<>(); // true positives
    private final Series<String, Number> fpSeries = new Series<>(); // false positives

    @Override
    public void init() {
        tpSeries.setName("TP");
        fpSeries.setName("FP");
        appManager.addViewFactory(HOME_VIEW, () -> {
            FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.SEARCH.text,
                    e -> train());

            Chart chart = createChart();

            Label label = new Label("Hello, Tribuo with Gluon Mobile!");

            VBox root = new VBox(20, label, chart);
            root.setAlignment(Pos.CENTER);

            View view = new View(root) {
                @Override
                protected void updateAppBar(AppBar appBar) {
                    appBar.setTitleText("Gluon Mobile");
                }
            };

            fab.showOn(view);

            return view;
        });
    }

    @Override
    public void start(Stage stage) {
        appManager.start(stage);
    }

    private void postInit(Scene scene) {
        Swatch.LIGHT_GREEN.assignTo(scene);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());

        if (Platform.isDesktop()) {
            Dimension2D dimension2D = DisplayService.create()
                    .map(DisplayService::getDefaultDimensions)
                    .orElse(new Dimension2D(640, 480));
            scene.getWindow().setWidth(dimension2D.getWidth());
            scene.getWindow().setHeight(dimension2D.getHeight());
        }
    }

    private void train() {
        tpSeries.getData().clear();
        fpSeries.getData().clear();
        Thread thread = new Thread(){
            @Override public void run() {
                try {
                    URL dataUrl = Main.class.getResource("/bezdekIris.data");
                    var irisHeaders = new String[]{"sepalLength", "sepalWidth", "petalLength", "petalWidth", "species"};
                    ListDataSource<org.tribuo.classification.Label> irisData
                            = new CSVLoader<>(new LabelFactory()).loadDataSource(dataUrl, irisHeaders[4], irisHeaders);
                    TrainTestSplitter<org.tribuo.classification.Label> irisSplitter = new TrainTestSplitter<>(irisData, 0.7, 1L);
                    MutableDataset<org.tribuo.classification.Label> trainData = new MutableDataset<>(irisSplitter.getTrain());
                    MutableDataset<org.tribuo.classification.Label> testData = new MutableDataset<>(irisSplitter.getTest());
                    var cartTrainer = new CARTClassificationTrainer();
                    TreeModel<org.tribuo.classification.Label> tree = cartTrainer.train(trainData);

                    var evaluator = new LabelEvaluator();
                    LabelEvaluation evaluation = evaluator.evaluate(tree, testData);

                    for (org.tribuo.classification.Label label : trainData.getOutputs()) {
                        double f1 = evaluation.f1(label);
                        double fn = evaluation.fn(label);
                        double fp = evaluation.fp(label);
                        double tn = evaluation.tn(label);
                        double tp = evaluation.tp(label);
                        javafx.application.Platform.runLater(() -> {
                            tpSeries.getData().add(new Data<>(label.getLabel(), tp));
                            fpSeries.getData().add(new Data<>(label.getLabel(), fp));
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } ;
        thread.start();
    }

    private Chart createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(tpSeries);
        chartData.add(fpSeries);
        chart.setData(chartData);
        chart.setAnimated(false);
        chart.setTitle("Iris");
        return chart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
