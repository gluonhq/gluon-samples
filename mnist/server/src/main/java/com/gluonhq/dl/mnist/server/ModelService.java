package com.gluonhq.dl.mnist.server;

import com.gluonhq.dl.mnist.logic.ModelUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

public class ModelService {
         private static final Logger LOGGER = Logger.getLogger(ModelService.class.getName());
             private static final String MODEL_LOCATION = System.getProperty("modelFile", "/tmp/model.zip");
    private MultiLayerNetwork model;
    private ModelUtils utils;

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

    public INDArray predict (byte[] raw) throws IOException {
        return utils.output(model, new ByteArrayInputStream(raw));         
    }
    
}
