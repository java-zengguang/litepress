package com.zg.database.ml.train;


import java.io.IOException;

public interface TrainModel {
    double trainModel(double[][] trainData) throws IOException;

    double doModel(double[] inputData) throws IOException;

    void clearModel();

}
