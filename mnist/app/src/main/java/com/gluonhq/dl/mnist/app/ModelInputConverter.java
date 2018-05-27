package com.gluonhq.dl.mnist.app;

import com.gluonhq.connect.converter.InputStreamInputConverter;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModelInputConverter extends InputStreamInputConverter<MultiLayerNetwork> {

    private static final Logger LOGGER = Logger.getLogger(ModelInputConverter.class.getName());

    @Override
    public MultiLayerNetwork read() {
        try (InputStream is = getInputStream()) {
            return ModelSerializer.restoreMultiLayerNetwork(is);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Something went wrong while reading model from InputStream.", e);
            throw new RuntimeException(e);
        }
    }
}
