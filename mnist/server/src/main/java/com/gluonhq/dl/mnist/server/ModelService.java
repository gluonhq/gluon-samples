package com.gluonhq.dl.mnist.server;

import com.gluonhq.dl.mnist.logic.ModelUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

@Startup
@Singleton
public class ModelService {
         private static final Logger LOGGER = Logger.getLogger(ModelService.class.getName());
             private static final String MODEL_LOCATION = System.getProperty("modelFile", "/tmp/model.zip");
    private MultiLayerNetwork model;
    private ModelUtils utils;
    private ExecutorService executor = Executors.newFixedThreadPool(1); // synchronous for now 

    @PostConstruct
    public void postConstruct() {
        loadModel();
    }

    private void loadModel() {
        LOGGER.log(Level.INFO, "Loading model from file {0}", MODEL_LOCATION);

        try {
            File f = new File(MODEL_LOCATION);
            utils = new ModelUtils();

            if (f.exists()) {
                model = ModelSerializer.restoreMultiLayerNetwork(MODEL_LOCATION);
            } else {
                model = utils.createModel();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load model.", e);
        }
    }

    public byte[] getModel() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 512);
        ModelSerializer.writeModel(model, baos, false);
        return baos.toByteArray();
    }

    public String predict (byte[] raw) throws IOException {
        return utils.predict(model, new ByteArrayInputStream(raw));         
    }

    public String correctImage(byte[] raw, String label) {
        Runnable r = () -> {
            try {
                System.out.println("TRAIN "+raw.length+" bytes for label "+label);
                utils.trainModel(model, true, new ByteArrayInputStream(raw), Integer.valueOf(label));
                System.out.println("DONE TRAIN "+raw.length+" bytes for label "+label);
            } catch (Exception e) {
                Logger.getLogger(ModelService.class.getName()).log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
        };
        executor.submit(r);
        return "OK";
    }

    public void publishGradient(byte[] clientGradient) throws IOException {
        INDArray updateGradient = Nd4j.fromByteArray(clientGradient);
        Gradient gradient = model.gradient();
        // do some smart merging
        System.out.println("Thanks for the update, our model just became better");
    }
    
}
