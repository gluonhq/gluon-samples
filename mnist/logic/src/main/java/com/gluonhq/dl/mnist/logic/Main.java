package com.gluonhq.dl.mnist.logic;

import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class Main {

    public static String savedModelLocation = "trained_mnist_model.zip";
    static ModelUtils utils = new ModelUtils();
    static Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        File f = new File(savedModelLocation);
        MultiLayerNetwork model = null;
        if (f.exists()) {
            LOGGER.info("Model exists, restore it");
            model = ModelSerializer.restoreMultiLayerNetwork(savedModelLocation);
            utils.evaluateModel(model);

        } else {
            LOGGER.info("Create model");
            model = utils.createModel();
            LOGGER.info("Train model");
            utils.trainModel(model, true, null, -1);
            LOGGER.info("Save model");
            utils.saveModel(model, savedModelLocation);
            LOGGER.info("Eval model");
            utils.evaluateModel(model);
        }
        LOGGER.info("Run tests");
        runTests(model);
        LOGGER.info("Evaluate model after tests");
        utils.evaluateModel(model);
        LOGGER.info("Correct Image");
        correctImage(model, Main.class.getResourceAsStream("/mytestdata/3b.png"),3);
        utils.evaluateModel(model);

    }
    
    private static void runTests(MultiLayerNetwork model) throws Exception {
        InputStream five = Main.class.getResourceAsStream("/mytestdata/5599.png");
        InputStream sixa = Main.class.getResourceAsStream("/mytestdata/6a.png");
        InputStream sixb = Main.class.getResourceAsStream("/mytestdata/6b.png");
        sixa.mark(Integer.MAX_VALUE);
        sixb.mark(Integer.MAX_VALUE);
        String f = utils.predict(model, five);
        LOGGER.info("expected 5? "+f);
        String p0 = utils.predict(model, sixa, true);
        String p1 = utils.predict(model, sixb, true);
        LOGGER.info("expected 6 or retrain: p0 = "+p0+" and p1 = "+p1);

        boolean single = false;
        if (single) {
            if (!"6".equals(p0)) {
                LOGGER.info("Retrain for 6a");
                sixa.reset();
                utils.correctImage(model, true, sixa, 6);

            }
            if (!"6".equals(p1)) {
                LOGGER.info("Retrain for 6b");
                sixb.reset();
                utils.correctImage(model, true, sixb, 6);

            }
        } else if (!"6".equals(p0) || (!"6".equals(p1))) {
            sixa.reset();
            sixb.reset();
            List<InputStream> is = new LinkedList<>();
            is.add(sixa);
            is.add(sixb);
            List<Integer> labels = new LinkedList<>();
            labels.add(6);
            labels.add(6);
            utils.trainModel(model, true, is, labels);
        }

        sixa.reset();
        sixb.reset();
        utils.output(model, sixa, true);
        utils.output(model, sixb, true);
    }

    private static void correctImage(MultiLayerNetwork model, InputStream raw, int label) throws Exception {
        raw.mark(Integer.MAX_VALUE);
        String p0 = utils.predict(model, raw);
        LOGGER.info("p0 = " + p0);
        raw.reset();
        utils.correctImage(model, true, raw, label);
        raw.reset();
        String p1 = utils.predict(model, raw);
        LOGGER.info("p1 = " + p1);
        Thread.sleep(60000);
        raw.reset();
        String p2 = utils.predict(model, raw);
        LOGGER.info("p2 = " + p2);
    }
}
