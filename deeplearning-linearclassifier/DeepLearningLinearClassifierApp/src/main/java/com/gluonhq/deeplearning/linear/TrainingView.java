package com.gluonhq.deeplearning.linear;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.InputStreamInputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class TrainingView extends View {

    private final Label label;

    public TrainingView(String name) {
        super(name);

        label = new Label();

        Button button = new Button("train network model");
        button.setOnAction(e -> {
            Task task = train();
            button.disableProperty().bind(task.runningProperty());
        });

        VBox controls = new VBox(15.0, label, button);
        controls.setAlignment(Pos.CENTER);

        setCenter(controls);
    }

    @Override
    protected void updateAppBar(AppBar appBar) {
        appBar.setTitleText("Deep Learning - Linear Classifier");
    }

    private Task train() {
        label.setText("configuring model...");

        Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                int batchSize = 50;
                int evalSize = 50;

                // load training data
                RecordReader rrTrain = new CSVRecordReader();
                rrTrain.initialize(new InputStreamInputSplit(TrainingView.class.getResourceAsStream("/linear_data_train.csv")));
                DataSetIterator iterTrain = new RecordReaderDataSetIterator(rrTrain, batchSize, 0, 2);

                // load evaluation data
                RecordReader rrEval = new CSVRecordReader();
                rrEval.initialize(new InputStreamInputSplit(TrainingView.class.getResourceAsStream("/linear_data_eval.csv")));
                DataSetIterator iterEval = new RecordReaderDataSetIterator(rrEval, evalSize, 0, 2);

                long seed = 123L;
                double learningRate = 0.01;
                int numEpochs = 30;
                int numInputs = 2;
                int numHiddenNodes = 20;
                int numOutputs = 2;

                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                        .seed(seed)
                        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                        .updater(new Nesterovs.Builder()
                                .learningRate(learningRate)
                                .momentum(0.9)
                                .build())
                        .list()
                        .layer(0, new DenseLayer.Builder()
                                .nIn(numInputs)
                                .nOut(numHiddenNodes)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.RELU)
                                .build())
                        .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nIn(numHiddenNodes)
                                .nOut(numOutputs)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.SOFTMAX)
                                .build())
                        .pretrain(false)
                        .backprop(true)
                        .build();

                MultiLayerNetwork network = new MultiLayerNetwork(conf);
                network.init();
                network.setListeners((IterationListener) (model, iteration, epoch) -> {
                    Platform.runLater(() -> label.setText("Running iteration #" + iteration));
                });

                Platform.runLater(() -> label.setText("training model..."));
                for (int n = 0; n < numEpochs; n++) {
                    network.fit(iterTrain);
                }

                Platform.runLater(() -> label.setText("evaluating model..."));
                Evaluation evaluation = new Evaluation(numOutputs);
                while (iterEval.hasNext()) {
                    DataSet dataSet = iterEval.next();
                    INDArray features = dataSet.getFeatureMatrix();
                    INDArray labels = dataSet.getLabels();
                    INDArray predicted = network.output(features, false);
                    evaluation.eval(labels, predicted);
                }

                Platform.runLater(() -> label.setText("model evaluation result:\n" + evaluation.stats()));

                return null;
            }
        };

        Thread thread = new Thread(task,
                "Deeplearning NeuralNetwork Trainer");
        thread.setDaemon(true);
        thread.start();

        return task;
    }
}
