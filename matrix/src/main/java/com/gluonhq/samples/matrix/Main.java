package com.gluonhq.samples.matrix;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class Main extends Application {
    
    Series<Number, Number> series = new Series<>();

    private static void calcGrid(){
     
INDArray na = Nd4j.create(new float[]{1,2,3,4},new int[]{2,2});
INDArray nb = Nd4j.create(new float[]{3,-2,1,0},new int[]{2,2});
INDArray nc = na.mmul(nb);
System.err.println("A = "+na+"\n\nB = " + nb+"\n\nC = " + nc);

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

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        // ImageView imageView = new ImageView(new Image(Main.class.getResourceAsStream("openduke.png")));
        // imageView.setFitHeight(200);
        // imageView.setPreserveRatio(true);

        VBox root = new VBox(30, createChart(), label);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 640, 480);
        // scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        train();
        stage.show();
// calcGrid();
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
                    Platform.runLater(() -> series.getData().add(new XYChart.Data(x, y)));
                }
System.err.println("Calculate grid");
              //  calcGrid();
System.err.println("Calculate grid done");
            }
        };
        thread.start();
    }

    private Chart createChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);
        ObservableList<XYChart.Series<Number, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(series);
        lineChart.setData(chartData);
        return lineChart;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
