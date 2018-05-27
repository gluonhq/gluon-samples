package com.gluonhq.dl.mnist.app.service;

import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.dl.mnist.app.Model;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JosePereda
 */
public class Service {
    
    private static final Logger log = LoggerFactory.getLogger(Service.class);

    private final int height = 28;
    private final int width = 28;
    private final int channels = 1;
    private final int outputNum = 10;
    private boolean ios = false;

    private DataSetIterator dataIter;

    public Service() {
        ios = "ios".equals(System.getProperty("os.name").toLowerCase());
        System.out.println("create service, ios? "+ios+" and os.name = "+System.getProperty("os.name"));
    }
    
    public StringProperty predictLocal (Model model, File image) throws IOException {
        System.out.println("PREDICT aske1d, image = "+image);
        // Scale pixel values to 0-1 -- not yet working on iOS due to opencv conflicts !!
        INDArray row = null;
        if (!ios) {
            try {
                System.out.println("p2");
                NativeImageLoader loader = new NativeImageLoader(width, height, channels, true);
                System.out.println("p3");
                ImagePreProcessingScaler scaler = new ImagePreProcessingScaler(1, 0);
                System.out.println("p4");
                row = loader.asRowVector(image);
                System.out.println("row before scalar = " + row);
                scaler.transform(row);
                System.out.println("row after scalar = " + row);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else { //ios
            float[] d = new float[28 * 28];
            for (int i = 0; i < 28 * 28; i++) {
                d[i] = (float) Math.random();
            }
            row = new NDArray(d);
        }

        final MultiLayerNetwork nnModel = model.getNnModel();
        INDArray predict = nnModel.output(row);
        System.out.println("model prediciton = "+predict);
        String answer = String.valueOf(nnModel.predict(row)[0]);
        return new SimpleStringProperty(answer);
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

    
}
