package com.gluonhq.dl.mnist.app;

import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunctionObject;
import com.gluonhq.connect.GluonObservableObject;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

public class Model {

    /**
     * @return the currentImageFile
     */
    public File getCurrentImageFile() {
        return currentImageFile;
    }

    /**
     * @param currentImageFile the currentImageFile to set
     */
    public void setCurrentImageFile(File currentImageFile) {
        this.currentImageFile = currentImageFile;
    }

    private final Logger LOGGER = Logger.getLogger(Model.class.getName());
    private final ObjectProperty<MultiLayerNetwork> nnModel;
    private File currentImageFile;

    public Model() {
        nnModel = new SimpleObjectProperty<>();
        try {
            loadModelRemote();
            // loadModelLocal();
        } catch (Exception ex) {
            System.out.println("[JVDBG] ERROR LOADING MODEL");
            LOGGER.warning("error loading model");
            ex.printStackTrace();
        }
    }
    
    public final ObjectProperty<MultiLayerNetwork> nnModelProperty() {
        return nnModel;
    }

    public final MultiLayerNetwork getNnModel() {
        return nnModel.get();
    }
    
    private void loadModelRemote() {
        System.out.println("******LOAD TRAINED MODEL (remote)******");
        try {
            RemoteFunctionObject rf = RemoteFunctionBuilder.create("fetchModel")
                    .cachingEnabled(false)
                    .object();

            GluonObservableObject<MultiLayerNetwork> rfObject = rf.call(new ModelInputConverter());

            rfObject.stateProperty().addListener((obs, ov, nv) -> {
                System.out.println("LOADMODELREOTE, status = " + nv);
                switch (nv) {
                    case SUCCEEDED:
                        nnModel.set(rfObject.get());
                        System.out.println("******RETRIEVED TRAINED MODEL REMOTELY******");
                        System.out.println("model = " + nnModel.get());
                        System.out.println("bs = " + nnModel.get().summary());
                        break;
                    case FAILED:
                        rfObject.getException().printStackTrace();
                        break;
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    private void loadModelLocal() {
        System.out.println("******LOAD TRAINED MODEL (local)******");
        try {
            InputStream is = Model.class.getResourceAsStream("/mymodel.zip");
            MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(is);
            is.close();
            nnModel.set(network);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
