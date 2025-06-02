package com.zg.litepress.ml.factory;

import com.zg.litepress.ml.config.TrainConfig;
import com.zg.litepress.ml.train.TrainModel;
import com.zg.litepress.ml.train.TrainModelDl4j;
import com.zg.litepress.ml.train.TrainModelEncog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TrainModelFactory {

    private static Map<String, TrainModel> modelMap = new HashMap<>();

    private static TrainModelFactory trainModelFactory = null;

    public static TrainModelFactory getInstance() {
        if (trainModelFactory == null) {
            trainModelFactory = new TrainModelFactory();
        }
        return trainModelFactory;
    }

    private TrainModelFactory() {

    }

    public TrainModel getTrainModelInstance(TrainConfig trainConfig) throws IOException {
        TrainModel trainModel = null;
        if (modelMap.containsKey(trainConfig.modelID)) {
            trainModel = modelMap.get(trainConfig.modelID);
        } else {
            if ("dl4j".equals(trainConfig.modelType)) {
                trainModel = new TrainModelDl4j(trainConfig.input, trainConfig.output, trainConfig.hiddenLayer, trainConfig.timeOut, trainConfig.accuracyRate, trainConfig.localFilePath);
            } else {
                trainModel = new TrainModelEncog(trainConfig.input, trainConfig.output, trainConfig.hiddenLayer, trainConfig.timeOut, trainConfig.accuracyRate, trainConfig.localFilePath);
            }
            if (trainModel != null) {
                modelMap.put(trainConfig.modelID, trainModel);
            }
        }
        return trainModel;
    }


    public void clearModel(String modelID) {
        if (modelMap.containsKey(modelID)) {
            modelMap.remove(modelID);
        }
    }

}
