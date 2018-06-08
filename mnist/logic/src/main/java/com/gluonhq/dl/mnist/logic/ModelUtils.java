package com.gluonhq.dl.mnist.logic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.listener.impl.LogRecordListener;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 *
 * @author JosePereda
 */
public class ModelUtils {

    static String DATA_PATH;
    public final int height = 28;
    public final int width = 28;
    public final int channels = 1;
    public final int rngseed = 123;
    public final Random randNumGen = new Random(rngseed);
    public final int batchSize = 128;
    public final int outputNum = 10;
    public final int numEpochs = 2;
    
    private final List<TrainRequest> trainRequests = new LinkedList<>();
    private final AtomicBoolean inTraining = new AtomicBoolean(false);
    
    private static final Logger LOGGER = Logger.getLogger(ModelUtils.class.getName());

    static {
        String tmpDir = System.getProperty("user.home");
        DATA_PATH = tmpDir + File.separator + "mnist"+File.separator;
        System.out.println("datapath = "+DATA_PATH);
    }

    public MultiLayerNetwork createModel() throws Exception {


        // Build Our Neural Network
        LOGGER.info("**** Build Model ****");

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngseed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Nesterovs(0.006, 0.9))
                .l2(1e-4)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(height * width)
                        .nOut(100)
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(100)
                        .nOut(outputNum)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .pretrain(false)
                .backprop(true)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        MultiLayerNetwork answer = new MultiLayerNetwork(conf);
        return answer;
    }

    public void train() {
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        List<TrainRequest> toProcess = new LinkedList<>();
                        synchronized (trainRequests) {
                            if (trainRequests.isEmpty()) {
                                trainRequests.wait();
                            }
                            toProcess.addAll(trainRequests);
                            trainRequests.clear();
                        }
                        List<INDArray> features = new ArrayList<>(toProcess.size());
                        List<INDArray> labels = new ArrayList<>(toProcess.size());
                        for (TrainRequest request : toProcess) {
                            NativeImageLoader loader = new NativeImageLoader(width, height, channels);
                            DataNormalization scaler = request.invert ? new ImagePreProcessingScaler(1, 0) : new ImagePreProcessingScaler(0, 1);
                            INDArray f = loader.asMatrix(request.b);
                            scaler.transform(f);
                            features.add(f);
                            INDArray l = Nd4j.create(1, 10);
                            l.putScalar(Integer.valueOf(request.label), 1.0);
                            labels.add(l);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    public void trainModel(MultiLayerNetwork model, boolean invertColors, InputStream customImage, int customLabel) throws Exception {
        List<INDArray> extraFeatures = new LinkedList<>();
        List<Integer> extraLabels = new LinkedList<>();
        final INDArray[] customData = {null, null};
        if (customImage != null) {
            NativeImageLoader loader = new NativeImageLoader(width, height, channels);
            DataNormalization scaler = invertColors ? new ImagePreProcessingScaler(1, 0) : new ImagePreProcessingScaler(0, 1);
            customData[0] = loader.asMatrix(customImage);
            scaler.transform(customData[0]);
            customData[1] = Nd4j.create(1, 10);
            customData[1].putScalar(customLabel, 1.0);
            extraFeatures.add(customData[0]);
            extraLabels.add(customLabel);
        }
        trainModel(model, extraFeatures, extraLabels);
    }

    public void trainModel(MultiLayerNetwork model, boolean invertColors, List<InputStream> customImage,
            List<Integer> customLabel) throws Exception {

        List<INDArray> extraFeatures = new LinkedList<>();
        List<Integer> extraLabels = new LinkedList<>();
        for (int i = 0; i < customImage.size(); i++) {
            NativeImageLoader loader = new NativeImageLoader(width, height, channels);
            DataNormalization scaler = invertColors ? new ImagePreProcessingScaler(1, 0) : new ImagePreProcessingScaler(0, 1);
            INDArray feature = loader.asMatrix(customImage.get(i));
            scaler.transform(feature);
            extraFeatures.add(feature);
            extraLabels.add(customLabel.get(i));
        }
        trainModel(model, extraFeatures, extraLabels);
    }

    private void trainModel(MultiLayerNetwork model, List<INDArray> extraFeatures, List<Integer> extraLabels) throws Exception {
        /*
        This class downloadData() downloads the data
        stores the data in java's tmpdir
        15MB download compressed
        It will take 158MB of space when uncompressed
        The data can be downloaded manually here
        http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz
         */
        ModelUtils.downloadData();
        // Define the File Paths
        File trainData = new File(DATA_PATH + "/mnist_png/training");
        System.out.println("abspath traindata = "+trainData.getAbsolutePath());
        // Define the FileSplit(PATH, ALLOWED FORMATS,random)
        FileSplit train = new FileSplit(trainData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);

        // Extract the parent path as the image label
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, labelMaker);

        // Initialize the record reader
        recordReader.initialize(train);

        // DataSet Iterator
        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, outputNum) {
            public DataSet next(int batchSize) {
                DataSet ds = super.next(batchSize);
                if (extraFeatures.size() > 0) {
//                 if (customData[0] != null) {
                    // append our custom data to each batch
                    INDArray features = ds.getFeatures();
                    INDArray labels = ds.getLabels();
                    for (int i = 0; i < extraFeatures.size(); i++) {
                        features = Nd4j.concat(0, features, extraFeatures.get(i));
                        INDArray ln = Nd4j.create(1, 10);
                        ln.putScalar(extraLabels.get(i), 1.0);
                        labels = Nd4j.concat(0, labels, ln);
                    }
                    return new DataSet(features, labels);
                }
                return ds;
            }
        };

        // Scale pixel values to 0-1
  //       DataNormalization scaler = new ImagePreProcessingScaler(invertColors ? 1 : 0, invertColors ? 0 : 1);
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);
        // The Score iteration Listener will log
        // output to show how well the network is training
        model.setListeners(new ScoreIterationListener(100));

        INDArray oldParameters = model.params().dup();

        LOGGER.info("*****TRAIN MODEL********");
        for (int i = 0; i < numEpochs || !extraFeatures.isEmpty(); i++) {
            model.fit(dataIter);
            if (!extraFeatures.isEmpty()) {
                boolean allgood = true;
                for (int j = 0; j < extraFeatures.size(); j++) {
                    int p = model.predict(extraFeatures.get(j))[0];
                    int correct = extraLabels.get(j);
                    System.out.println("custom feature " + correct + " predicted as " + p);
                    if (p != correct) {
                        allgood = false;
                    }
                }
                if (allgood) break;
                System.out.println("bummer, at least one prediction for extra data failed after try "+i+" with numEpochs "+numEpochs);
                if (i > 2 *numEpochs) {
                    System.out.println("No good result after "+i+" tries, bail");
                    break;
                }
            }
        }

        INDArray newParameter = model.params().dup();
        INDArray update = newParameter.sub(oldParameters);
        System.out.println(update);
        saveModel(model, Main.savedModelLocation);
        // We could then send the update over the network and apply it this way:
        // model.setParams(model.params().add(update));
    }
    
    public void saveModel(MultiLayerNetwork model, String location) throws IOException {
        LOGGER.info("******SAVE TRAINED MODEL******");
        // Details

        // Where to save model
        File locationToSave = new File(location);

        // boolean save Updater
        boolean saveUpdater = false;

        // ModelSerializer needs modelname, saveUpdater, Location

        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }
    
    public void evaluateModel(MultiLayerNetwork model, boolean invertColors) throws IOException {
        LOGGER.info("******EVALUATE MODEL******");

        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        ImageRecordReader recordReader = new ImageRecordReader(height,width,channels,labelMaker);
        recordReader.setListeners(new LogRecordListener());

        // Initialize the record reader
        // add a listener, to extract the name

        File testData = new File(DATA_PATH + "/mnist_png/testing");
        FileSplit test = new FileSplit(testData,NativeImageLoader.ALLOWED_FORMATS,randNumGen);

        // The model trained on the training dataset split
        // now that it has trained we evaluate against the
        // test data of images the network has not seen

        recordReader.initialize(test);
        DataNormalization scaler = new ImagePreProcessingScaler(invertColors ? 1 : 0, invertColors ? 0 : 1);
        DataSetIterator testIter = new RecordReaderDataSetIterator(recordReader,batchSize,1,outputNum);
        scaler.fit(testIter);
        testIter.setPreProcessor(scaler);

        /*
        log the order of the labels for later use
        In previous versions the label order was consistent, but random
        In current verions label order is lexicographic
        preserving the RecordReader Labels order is no
        longer needed left in for demonstration
        purposes
        */
        LOGGER.info(recordReader.getLabels().toString());

        // Create Eval object with 10 possible classes
        Evaluation eval = new Evaluation(outputNum);


        // Evaluate the network
        while (testIter.hasNext()) {
            DataSet next = testIter.next();
            INDArray output = model.output(next.getFeatureMatrix());
            // Compare the Feature Matrix from the model
            // with the labels from the RecordReader
            eval.eval(next.getLabels(), output);

        }

        LOGGER.info(eval.stats());
    }
    
    /**
     * Accept any input stream. Scale data to width*height size and each pixel to 0..1
     * (or actually 1..0 since we invert)
     * @param model
     * @param is
     * @return
     * @throws IOException 
     */
    public INDArray output(MultiLayerNetwork model, InputStream is) throws IOException {
        return output(model, is, false);
    }
    /**
     * Accept any input stream. Scale data to width*height size and each pixel to 0..1
     * (or actually 1..0 since we invert)
     * @param model
     * @param is
     * @return
     * @throws IOException 
     */
    public INDArray output(MultiLayerNetwork model, InputStream is, boolean invert) throws IOException {
        NativeImageLoader loader = new NativeImageLoader(width, height, channels);
        INDArray nd = loader.asRowVector(is);
        return output(model, nd, invert);
    }
    
    public INDArray output(MultiLayerNetwork model, String f) throws IOException {
        NativeImageLoader loader = new NativeImageLoader(width, height, channels);
        INDArray nd = loader.asRowVector(new File(f));
        return output(model, nd, false);
    }

    private INDArray output(MultiLayerNetwork model, INDArray nd, boolean invert) {
        // invert black-white 
        DataNormalization scaler = new ImagePreProcessingScaler(invert? 1:0, invert? 0: 1);
        scaler.transform(nd);
//        preprocess(nd);
//        System.out.println("nd = "+nd);
        INDArray output = model.output(nd);
//        System.out.println("=== output = "+output+" -> prediction = "+model.predict(nd)[0]);
        System.out.println(" prediction = "+model.predict(nd)[0]);
        return output;
    }
    
    public String predict(MultiLayerNetwork model, InputStream is) throws IOException {
        return predict(model, is, false);
    }
    
    public String predict(MultiLayerNetwork model, InputStream is, boolean invertColors) throws IOException {
        NativeImageLoader loader = new NativeImageLoader(width, height, channels);
        INDArray nd = loader.asRowVector(is);
        return predict(model, nd, invertColors);
    }

    public String predict(MultiLayerNetwork model, String f) throws IOException {
        NativeImageLoader loader = new NativeImageLoader(width, height, channels);
        INDArray nd = loader.asRowVector(new File(f));
        return predict(model, nd);
    }

    private String predict(MultiLayerNetwork model, INDArray nd) {
        return predict (model, nd, false);
    }
    
    private String predict(MultiLayerNetwork model, INDArray nd, boolean invertColors) {
        // invert black-white 
        DataNormalization scaler = new ImagePreProcessingScaler(invertColors? 1 : 0, 
        invertColors ? 0 :1);
        scaler.transform(nd);
        preprocess(nd);
        System.out.println("I have to predict "+nd);
        int p = model.predict(nd)[0];
        System.out.println("prediction = "+model.predict(nd)[0]);
        return String.valueOf(p);
    }
    
    public static void downloadData() throws Exception {
        //Create directory if required
        File directory = new File(DATA_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }

        //Download file:
        String archizePath = DATA_PATH + "/mnist_png.tar.gz";
        File archiveFile = new File(archizePath);
        String extractedPath = DATA_PATH + "/mnist_png";
        File extractedFile = new File(extractedPath);

        if (!archiveFile.exists()) {
            System.out.println("Starting data download (15MB)...");
            getMnistPNG();
            //Extract tar.gz file to output directory
            extractTarGz(archizePath, DATA_PATH);
        } else {
            //Assume if archive (.tar.gz) exists, then data has already been extracted
            System.out.println("Data (.tar.gz file) already exists at " + archiveFile.getAbsolutePath());
            if (!extractedFile.exists()) {
                //Extract tar.gz file to output directory
                extractTarGz(archizePath, DATA_PATH);
            } else {
                System.out.println("Data (extracted) already exists at " + extractedFile.getAbsolutePath());
            }
        }

    }

    private static final int BUFFER_SIZE = 4096;

    private static void extractTarGz(String filePath, String outputPath) throws IOException {
        int fileCount = 0;
        int dirCount = 0;
        System.out.print("Extracting files");
        try (TarArchiveInputStream tais = new TarArchiveInputStream(
                new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(filePath))))) {
            TarArchiveEntry entry;

            /**
             * Read the tar entries using the getNextEntry method *
             */
            while ((entry = (TarArchiveEntry) tais.getNextEntry()) != null) {
                //System.out.println("Extracting file: " + entry.getName());

                //Create directories as required
                if (entry.isDirectory()) {
                    new File(outputPath + entry.getName()).mkdirs();
                    dirCount++;
                } else {
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];

                    FileOutputStream fos = new FileOutputStream(outputPath + entry.getName());
                    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = tais.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
                    fileCount++;
                }
                if (fileCount % 1000 == 0) {
                    System.out.print(".");
                }
            }
        }

        System.out.println("\n" + fileCount + " files and " + dirCount + " directories extracted to: " + outputPath);
    }

    public static void getMnistPNG() throws IOException {
        String tmpDirStr = System.getProperty("java.io.tmpdir");
        String archizePath = DATA_PATH + "/mnist_png.tar.gz";

        if (tmpDirStr == null) {
            throw new IOException("System property 'java.io.tmpdir' does specify a tmp dir");
        }
        String url = "http://github.com/myleott/mnist_png/raw/master/mnist_png.tar.gz";
        File f = new File(archizePath);
        File dir = new File(tmpDirStr);
        if (!f.exists()) {
            HttpClientBuilder builder = HttpClientBuilder.create();
            CloseableHttpClient client = builder.build();
            try (CloseableHttpResponse response = client.execute(new HttpGet(url))) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (FileOutputStream outstream = new FileOutputStream(f)) {
                        entity.writeTo(outstream);
                        outstream.flush();
                        outstream.close();
                    }
                }

            }
            System.out.println("Data downloaded to " + f.getAbsolutePath());
        } else {
            System.out.println("Using existing directory at " + f.getAbsolutePath());
        }

    }

    // values lower than a threshold (.4) are blacked.
    private void preprocess(INDArray nd) {
        for (int i = 0; i < 28; i++) {
            for (int j = 0; j < 28; j++) {
                int idx = i * 28 + j;
                double orig = nd.getDouble(idx);
                if (orig < .4) {
                    nd.putScalar(idx, 0d);
                } else {
//                    nd.putScalar(idx,.99d);
                }

            }
        }
    }

}
