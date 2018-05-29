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
            utils.trainModel(model, true, null, -1);
            utils.saveModel(model, savedModelLocation);
            utils.evaluateModel(model, true);
        }
        runTests(model);
    }
    
    private static void runTests(MultiLayerNetwork model) throws Exception {
        InputStream sixa = Main.class.getResourceAsStream("/mytestdata/6a.png");
        InputStream sixb = Main.class.getResourceAsStream("/mytestdata/6b.png");
        sixa.mark(Integer.MAX_VALUE);
        sixb.mark(Integer.MAX_VALUE);

        utils.trainModel(model, true, sixa, 6);
        utils.trainModel(model, true, sixb, 6);

        sixa.reset();
        sixb.reset();
        utils.output(model, sixa);
        utils.output(model, sixb);
    }

}
