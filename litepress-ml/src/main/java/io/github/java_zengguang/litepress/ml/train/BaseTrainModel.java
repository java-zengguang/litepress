package io.github.java_zengguang.litepress.ml.train;

import java.util.List;

public abstract class BaseTrainModel implements TrainModel {
     Integer input;
     Integer output;
     List<Integer> hiddenLayer;
     Long timeOut=60*1000L;
     Double accuracyRate=0.99;
     String localFilePath;
}
