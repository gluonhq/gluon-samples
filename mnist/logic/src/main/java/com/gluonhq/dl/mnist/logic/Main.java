package com.gluonhq.dl.mnist.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class Main {

    static String savedModelLocation = "trained_mnist_model.zip";
    static ModelUtils utils = new ModelUtils();

    public static void main(String[] args) throws Exception {
        File f = new File(savedModelLocation);
        MultiLayerNetwork model = null;
        if (f.exists()) {
            model = ModelSerializer.restoreMultiLayerNetwork(savedModelLocation);

        } else {
            model = utils.createModel();
            utils.trainModel(model);
            utils.saveModel(model, savedModelLocation);
            utils.evaluateModel(model);
        }
        runTests(model);
    }
    
    private static void runTests(MultiLayerNetwork model) throws IOException {
        InputStream sixa = Main.class.getResourceAsStream("/mytestdata/6a.png");
        utils.output(model, sixa);
        InputStream sixb = Main.class.getResourceAsStream("/mytestdata/6b.png");
        utils.output(model, sixb);
    }

}
