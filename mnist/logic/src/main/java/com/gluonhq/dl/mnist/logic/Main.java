package com.gluonhq.dl.mnist.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class Main {

    public static String savedModelLocation = "trained_mnist_model.zip";
    static ModelUtils utils = new ModelUtils();

    public static void main(String[] args) throws Exception {
        File f = new File(savedModelLocation);
        MultiLayerNetwork model = null;
        if (f.exists()) {
            System.out.println("Model exists, restore it");
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
        InputStream five = Main.class.getResourceAsStream("/mytestdata/5599.png");
        InputStream sixa = Main.class.getResourceAsStream("/mytestdata/6a.png");
        InputStream sixb = Main.class.getResourceAsStream("/mytestdata/6b.png");
        sixa.mark(Integer.MAX_VALUE);
        sixb.mark(Integer.MAX_VALUE);
        String f = utils.predict(model, five);
        System.out.println("FIVE? "+f);
        String p0 = utils.predict(model, sixa, true);
        String p1 = utils.predict(model, sixb, true);
        System.out.println("p0 = "+p0+" and p1 = "+p1);

        if (!"6".equals(p0)) {
            System.out.println("Retrain for 6a");
            sixa.reset();
            utils.trainModel(model, true, sixa, 6);

        }
        if (!"6".equals(p1)) {
            System.out.println("Retrain for 6b");
            sixb.reset();
            utils.trainModel(model, true, sixb, 6);

        }

        sixa.reset();
        sixb.reset();
        utils.output(model, sixa, true);
        utils.output(model, sixb, true);
    }

}
