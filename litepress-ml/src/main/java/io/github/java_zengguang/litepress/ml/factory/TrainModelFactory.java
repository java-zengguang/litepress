package io.github.java_zengguang.litepress.ml.factory;

import io.github.java_zengguang.litepress.ml.config.TrainConfig;
import io.github.java_zengguang.litepress.ml.train.TrainModel;
import io.github.java_zengguang.litepress.ml.train.TrainModelDl4j;
import io.github.java_zengguang.litepress.ml.train.TrainModelEncog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TrainModelFactory {



    private static TrainModelFactory trainModelFactory = null;

    public static TrainModelFactory getInstance() {
        if (trainModelFactory == null) {
            trainModelFactory = new TrainModelFactory();
        }
        return trainModelFactory;
    }

    private TrainModelFactory() {

    }



    public TrainModel createTrainModel(TrainConfig trainConfig) throws IOException {
        TrainModel trainModel = null;
        if ("dl4j".equals(trainConfig.modelType)) {
            trainModel = new TrainModelDl4j(trainConfig.input, trainConfig.output, trainConfig.hiddenLayer, trainConfig.timeOut, trainConfig.accuracyRate, trainConfig.localFilePath);
        } else {
            trainModel = new TrainModelEncog(trainConfig.input, trainConfig.output, trainConfig.hiddenLayer, trainConfig.timeOut, trainConfig.accuracyRate, trainConfig.localFilePath);
        }
        return trainModel;
    }




}
