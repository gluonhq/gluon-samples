package com.gluonhq.samples.matrix;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/*
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
*/
import java.net.URL;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.openblas.global.openblas_nolapack;

public class Main {
    private static final int nPointsPerAxis = 100;
    private static INDArray xyGrid = null; //x,y grid to calculate the output image. Needs to be calculated once, then re-used.
    public static void main(String[] args) {
        // String arch= "linux-x86_64";
        // String arch= "android-arm64";
        String arch= "arm64-v8a";
        System.err.println("Hello, main");
        try {
URL url = Loader.class.getResource(arch+"/libjnijavacpp.so");
// URL url2 = openblas_nolapack.class.getResource("linux-x86_64/libjniopenblas_nolapack.so");
URL url2 = openblas_nolapack.class.getResource("/org/bytedeco/openblas/"+arch+"/libjniopenblas_nolapack.so");
System.err.println("url = " + url);
System.err.println("url2 = " + url2);
calcGrid();
            // System.loadLibrary("jnijavacpp");
        } catch (Throwable t) {
            System.err.println("Bummer!");
            t.printStackTrace();
        }
        System.err.println("Hello main done");
    }

    private static void calcGrid(){
        System.out.println(Nd4j.zeros(2));

        // x coordinates of the pixels for the NN.
        INDArray xPixels = Nd4j.linspace(0, 1.0, nPointsPerAxis, DataType.DOUBLE);
        // y coordinates of the pixels for the NN.
        INDArray yPixels = Nd4j.linspace(0, 1.0, nPointsPerAxis, DataType.DOUBLE);
        //create the mesh:
        INDArray [] mesh = Nd4j.meshgrid(xPixels, yPixels);
        xyGrid = Nd4j.vstack(mesh[0].ravel(), mesh[1].ravel()).transpose();
System.err.println("xyGrid = "+xyGrid);
System.err.println("xyGrid = "+xyGrid);
INDArray na = Nd4j.create(new float[]{1,2,3,4},new int[]{2,2});
INDArray nb = Nd4j.create(new float[]{3,-2,1,0},new int[]{2,2});
INDArray nc = na.mmul(nb);
System.err.println("A = "+na+"\n\nB = " + nb+"\n\nC = " + nc);

    }

}

/* extends Application {



    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

        ImageView imageView = new ImageView(new Image(Main.class.getResourceAsStream("openduke.png")));
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        VBox root = new VBox(30, imageView, label);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        train();
        stage.show();
// calcGrid();
    }

    private static void train() {
        Thread thread = new Thread(){
            @Override public void run() {
System.err.println("Calculate grid");
                calcGrid();
System.err.println("Calculate grid done");
            }
        };
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
*/
