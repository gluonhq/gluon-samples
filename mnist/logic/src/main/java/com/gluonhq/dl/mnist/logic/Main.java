package com.gluonhq.dl.mnist.logic;

import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
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
        byte[] b = Files.toByteArray(new File("/tmp/3-1528533059666"));
        correctImage(model, b, 3);
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

        boolean single = false;
        if (single) {
            if (!"6".equals(p0)) {
                System.out.println("Retrain for 6a");
                sixa.reset();
                utils.correctImage(model, true, sixa, 6);

            }
            if (!"6".equals(p1)) {
                System.out.println("Retrain for 6b");
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

    private static void correctImage(MultiLayerNetwork model, byte[] raw, int label) throws Exception {
        System.out.println("raw = "+raw);
        utils.correctImage(model, true, new ByteArrayInputStream(raw), label);

    }
}
