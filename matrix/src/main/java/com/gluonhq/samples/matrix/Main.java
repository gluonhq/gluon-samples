package com.gluonhq.samples.matrix;

import com.gluonhq.attach.display.DisplayService;
import com.gluonhq.attach.util.Platform;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import com.gluonhq.charm.glisten.visual.Swatch;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class Main extends MobileApplication {
    
    Series<Number, Number> series = new Series<>();

        @Override
    public void init() {
        addViewFactory(HOME_VIEW, () -> {
            FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.SEARCH.text,
                    e -> System.out.println("Search"));

            Chart chart = createChart();

            Label label = new Label("Hello, ND4J with Gluon Mobile!");

            VBox root = new VBox(20, chart, label);
            root.setAlignment(Pos.CENTER);

            View view = new View(root) {
                @Override
                protected void updateAppBar(AppBar appBar) {
                    appBar.setTitleText("Gluon Mobile and ND4J");
                }
            };

            fab.showOn(view);

            return view;
        });
        train();
    }
    @Override
    public void postInit(Scene scene) {
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

    private long multiplyMatrix(int dim) {
        double[] a = new double[dim * dim];
        double[] b = new double[dim * dim];
        for (int i = 0; i < dim * dim; i++) {
            a[i] = Math.random()-.5;
            b[i] = Math.random()-.5;
        }
        long s0 = System.currentTimeMillis();
        INDArray na = Nd4j.create(a,new int[]{dim,dim});
        INDArray nb = Nd4j.create(b,new int[]{dim,dim});
        INDArray nc = na.mmul(nb);
        long s1 = System.currentTimeMillis();
        long d = s1-s0;
        return d;
    }

    private void train() {
        Thread thread = new Thread(){
            @Override public void run() {
                double warmup = multiplyMatrix(1);
                warmup = multiplyMatrix(5);
                for (int i = 2; i < 40; i++) {
                    double t = multiplyMatrix(10*i);
                    final int x = 10*i;
                    final double y = t;
                    System.err.println("points "+i+", "+y);
                    javafx.application.Platform.runLater(() -> series.getData().add(new XYChart.Data(x, y)));
                }
            }
        };
        thread.start();
    }

    private Chart createChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("matrix dimension");
        yAxis.setLabel("Time (ms)");
        series.setName("nd4j");
        LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);
        ObservableList<XYChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(series);
        lineChart.setData(chartData);
        return lineChart;
    }

    public static void main(String[] args) {
        if (Platform.isAndroid()) {
            System.setProperty("org.bytedeco.javacpp.platform", "arm64-v8a");
            System.setProperty("org.bytedeco.javacpp.platform.library.path", "/lib");
            System.setProperty("org.bytedeco.javacpp.pathsFirst", "true");
        }
        launch(args);
    }
}
