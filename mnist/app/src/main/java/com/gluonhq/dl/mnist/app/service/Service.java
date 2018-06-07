package com.gluonhq.dl.mnist.app.service;

import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.VoidInputConverter;
import com.gluonhq.dl.mnist.app.Model;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author JosePereda
 */
public class Service {

    private Logger LOGGER = Logger.getLogger(Service.class.getName());
    private final int height = 28;
    private final int width = 28;
    private final int channels = 1;
    private final int outputNum = 10;
    private boolean ios = false;
    private boolean sendRawData = true;

    private DataSetIterator dataIter;

    public Service() {
        ios = "ios".equals(System.getProperty("os.name").toLowerCase());
        System.out.println("create service, ios? " + ios + " and os.name = " + System.getProperty("os.name"));
    }

    public StringProperty predictLocal(Model model, File image) throws IOException {
        System.out.println("PREDICT aske1d, image = " + image);
        String answer = "0";
        try {
            // for now we use the JavaFX PixelReader to read images, due to an issue in NativeImageLoader 
            // on Android that is fixed after 1.0.0-beta
            INDArray row = fromImage(image);
       

            
//            INDArray row = null;
//            NativeImageLoader loader = new NativeImageLoader(width, height, channels, true);
//            row = loader.asRowVector(image);
//            ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(1, 0);
//            System.out.println("BEFORE SCALING, row = "+row);
//            scaler.transform(row);
//            System.out.println("AFTER SCALING, row = "+row);

            final MultiLayerNetwork nnModel = model.getNnModel();
            INDArray predict = nnModel.output(row);
            System.out.println("model prediciton = " + predict);
            answer = String.valueOf(nnModel.predict(row)[0]);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new SimpleStringProperty(answer);
    }

    private INDArray fromImage(File image) {
        System.out.println("FROMImage called for "+image);
        Image im = new Image(image.toURI().toString(), width, height, false, true);
        PixelReader pr = im.getPixelReader();
        System.out.println("crated image, "+im.getWidth()+", "+im.getHeight());
        INDArray row = Nd4j.createUninitialized(width * height);
        boolean bw = pr.getColor(0, 0).getBrightness() < .5;
        System.out.println("bw = " + bw);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = pr.getArgb(i, j);
                Color c = pr.getColor(i, j);
                // int red = ((pixel >> 16) & 0xff);
                //  int green = ((pixel >> 8) & 0xff);
                //  int blue = (pixel & 0xff);

                //   int grayLevel = (int) (0.2162 * (double) red + 0.7152 * (double) green + 0.0722 * (double) blue) / 3;
                //   grayLevel = (int)((red + green + blue)/3.);
                //   grayLevel = 255 - grayLevel; // Inverted the grayLevel value here.
                //  int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                //    System.out.println("p[" + i + "][" + j + "]: " + pixel+" -> "+grayLevel+ "("+red+", "+green+", "+blue+")"+" -> "+c.getBrightness()+", "+c.getSaturation()+", "+c.getHue()+", "+c.getOpacity());
                row.putScalar(j * height + i, bw ? c.getBrightness() : 1 - c.getBrightness());
            }
        }
        return row;
    }
    
    public StringProperty predictRemote(Model model, File image) throws IOException {
        byte[] rawBody = Files.readAllBytes(image.toPath());
        StringProperty answer = new SimpleStringProperty();
        GluonObservableObject<String> classified = RemoteFunctionBuilder.create("classifyImage").rawBody(rawBody).object()
            .call(String.class);
        classified.stateProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue ov, Object t, Object t1) {
                System.out.println("state of classify response = " + t1);
                System.out.println("get = " + classified.get());
                answer.set(classified.get());
            }

        });
        return answer;
    }

    /**
     * Here, we can either update the local model and send a gradient to the cloud;
     * or we can send the raw data + label to the cloud.
     * @param model
     * @param image
     * @param label 
     */
    public void updateModel(Model model, File image, int label) {
        if (sendRawData) {
        updateWithRawData(image, label);
        } else {
        try {
//            NativeImageLoader loader = new NativeImageLoader(width, height, channels, true);
//            ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(1, 0);
//            INDArray row = loader.asRowVector(image);
//            scaler.transform(row);
            MultiLayerNetwork nnmodel = model.getNnModel();
            INDArray row = fromImage(image);
            
            INDArray labels = Nd4j.create(10);
            labels.putScalar(label, 1.0d);
            nnmodel.fit(row, labels);
            Gradient gradient = nnmodel.gradient();
            INDArray updateVector = gradient.gradient();
            GluonObservableObject<Void> function = RemoteFunctionBuilder.create("publishGradient")
                .cachingEnabled(false)
                .rawBody(Nd4j.toByteArray(updateVector))
                .object().call(new VoidInputConverter());
            function.stateProperty().addListener(new ChangeListener<ConnectState>() {
                @Override
                public void changed(ObservableValue<? extends ConnectState> ov, ConnectState t, ConnectState t1) {
                    System.out.println("status of update gradient request is now "+t1);
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        }
    }
    
    private void updateWithRawData(File image, int label) {
        try {
            byte[] rawBody = Files.readAllBytes(image.toPath());
            GluonObservableObject<Void> function = RemoteFunctionBuilder.create("trainImage")
                    .cachingEnabled(false)
                    .param("label", String.valueOf(label))
                    .rawBody(rawBody)
                    .object().call(new VoidInputConverter());
        } catch (IOException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
