package com.zg.litepress.ml.config;

import java.io.Serializable;
import java.util.List;

public class TrainConfig implements Serializable {
    public String modelType;  //模型类型  dl4j  encog
    public String modelID;
    public Integer input;
    public Integer output;
    public String localFilePath;
    public List<Integer> hiddenLayer;
    public Long timeOut=60*1000L;
    public Double accuracyRate=0.99;
}
