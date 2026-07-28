package io.github.java_zengguang.litepress.ml.config;

import java.io.Serializable;
import java.util.List;

public class TrainConfig implements Serializable {
    public String modelType;  //模型类型  dl4j  encog
    public String modelID;
    public Integer input;
    public Integer output;
    public String localFilePath;
    /**
     * 隐藏层结构。为 null 时不在构造期建网,而在 {@code trainModel} 时按样本量级自动选择
     * (见 {@link io.github.java_zengguang.litepress.ml.train.TrainModelDl4j#autoHiddenLayers});
     * 非 null 时按指定结构构建(向后兼容)。
     */
    public List<Integer> hiddenLayer;
    public Long timeOut=60*1000L;
    public Double accuracyRate=0.99;
}
