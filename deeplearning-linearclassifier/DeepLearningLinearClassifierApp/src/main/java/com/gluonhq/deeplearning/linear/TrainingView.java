/*
 * Copyright (c) 2018, Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class TrainingView extends View {

    private final Label label;

    public TrainingView() {

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
            protected Object call() {
              try {
                int batchSize = 50;
                int evalSize = 50;

                long seed = 123L;
                double learningRate = 0.01;
                int numEpochs = 30;
                int numInputs = 2;
                int numHiddenNodes = 20;
                int numOutputs = 2;
                


                MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                        .seed(seed)
                        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                        .updater(new Nesterovs(learningRate,0.9))
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
                network.setListeners(new ScoreIterationListener(100));
                      // load training data
                RecordReader rrTrain = new CSVRecordReader();
                rrTrain.initialize(new InputStreamInputSplit(TrainingView.class.getResourceAsStream("/linear_data_train.csv")));
                DataSetIterator iterTrain = new RecordReaderDataSetIterator(rrTrain, batchSize, 0, 2);

                // load evaluation data
                RecordReader rrEval = new CSVRecordReader();
                rrEval.initialize(new InputStreamInputSplit(TrainingView.class.getResourceAsStream("/linear_data_eval.csv")));
                DataSetIterator iterEval = new RecordReaderDataSetIterator(rrEval, evalSize, 0, 2);


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
              } catch (Throwable t) {
t.printStackTrace();
              }

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
