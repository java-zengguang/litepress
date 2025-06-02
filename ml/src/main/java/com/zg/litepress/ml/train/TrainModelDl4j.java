package com.zg.litepress.ml.train;

import com.zg.litepress.ml.util.DataDealUtils;

import com.zg.litepress.ml.util.OneHotUtil;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class TrainModelDl4j extends BaseTrainModel implements TrainModel {

    private MultiLayerNetwork network;


    public TrainModelDl4j(Integer input, Integer output, List<Integer> hiddenLayer, Long timeOut, Double accuracyRate,String localFilePath) throws IOException {
        this.input = input;
        this.output = output;
        this.localFilePath = localFilePath;
        this.hiddenLayer = hiddenLayer;
        this.timeOut = timeOut;
        this.accuracyRate = accuracyRate;
        initModel();
    }


    public void initModel() throws IOException {
        File file = new File(localFilePath);
        if (file.exists()) {
            Logger.info("加载模型");
            network = readToLocation(file);
        } else {
            Logger.info("加载模型");
            network = createNetwork();
        }
        network.init();
    }


    private MultiLayerNetwork createNetwork() {
        // Define the network configuration
        NeuralNetConfiguration.ListBuilder listBuilder = new NeuralNetConfiguration.Builder()
                .seed(123) // Set random seed for reproducibility
                .updater(new Adam(0.001)) // Use Adam optimizer with learning rate 0.001
                .list();

        //定义输入层
        Logger.info("输入层 输入" + input + " 输出 " + hiddenLayer.getFirst());
        listBuilder.layer(new DenseLayer.Builder().nIn(input).nOut(hiddenLayer.getFirst())
                .activation(Activation.RELU)
                .build());

        //定义隐藏层
        for (int i = 0; i < hiddenLayer.size() - 1; i++) {
            Logger.info("隐藏层 输入" + hiddenLayer.get(i) + " 输出 " + hiddenLayer.get(i + 1));
            listBuilder.layer(new DenseLayer.Builder()
                    .activation(Activation.RELU)
                    .nIn(hiddenLayer.get(i)).nOut(hiddenLayer.get(i + 1))
                    .build());
        }
        //定义输出层
        Logger.info("输出层 输入" + hiddenLayer.getLast() + " 输出 " + output);
        if (output > 1) {
            //多分类问题
            listBuilder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                    .activation(Activation.SOFTMAX)
                    .nIn(hiddenLayer.getLast()).nOut(output)
                    .build());
        } else {
            //二元分类
            listBuilder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                    .activation(Activation.SIGMOID)
                    .nIn(hiddenLayer.getLast()).nOut(output)
                    .build());
        }

        //创建模型
        MultiLayerNetwork model = new MultiLayerNetwork(listBuilder.build());
        return model;
    }

    public double trainModel(double[][] trainData) throws IOException {
        INDArray allData = Nd4j.create(trainData);
        // 使用 getColumns 方法来选择特定的列
        INDArray features = allData.getColumns(IntStream.rangeClosed(0, input - 1).toArray()); // 获取前 n 列作为特征
        INDArray labels = allData.getColumns(IntStream.rangeClosed(input, allData.columns() - 1).toArray()); // 获取从第 n 列到最后一列作为标签
        labels = OneHotUtil.oneHotEncode(labels, output); //归一化编码

        // 创建 DataSet
        DataSet dataSet = new DataSet(features, labels);
        // 创建 Evaluation 对象
        Evaluation eval = new Evaluation(2); // 假设二分类任务
        Date beginDate = new Date();
        do {
            // 简单的训练/测试集划分 (80%/20%)
            int splitIndex = (int) (dataSet.numExamples() * 0.8);
            DataSet trainDataSet = dataSet.splitTestAndTrain(splitIndex).getTrain();
            DataSet testDataSet = dataSet.splitTestAndTrain(splitIndex).getTest();
            network.fit(trainDataSet);
            Logger.info("Completed epoch ");
            //测试
            // 获取预测输出
            INDArray output = network.output(testDataSet.getFeatures());
            // 将真实标签转换为 one-hot 编码
            INDArray labelsOneHot = testDataSet.getLabels();
            // 更新 Evaluation 对象
            eval.eval(labelsOneHot, output);
            // 打印准确率
            Logger.info("Accuracy: " + eval.accuracy());

        } while (eval.accuracy() < accuracyRate && (new Date().getTime() - beginDate.getTime() < timeOut));
        Logger.info("Final score: " + network.score(dataSet));
        locationToSave(new File(localFilePath));
        return eval.accuracy();
    }


    private void locationToSave(File file) throws IOException {
        boolean saveUpdater = true; // Whether to save the updater (Adam in this case)
        ModelSerializer.writeModel(network, file, saveUpdater);
    }

    private MultiLayerNetwork readToLocation(File modelFile) throws IOException {
        // 从文件加载模型
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(modelFile);
        return model;
    }

    public double doModel(double[] inputData) throws IOException {
        // 从文件加载模型
        inputData = Arrays.stream(inputData).limit(input).toArray();
        INDArray indArray = Nd4j.create(inputData).reshape(1, inputData.length);
        INDArray outArray = network.output(indArray, false);
        outArray= OneHotUtil.oneHotDecode(outArray);
        return outArray.toDoubleVector()[0];
    }

    @Override
    public void clearModel() {
        File file=new File(localFilePath);
        file.deleteOnExit();
    }


    public static void main(String args[]) throws IOException, InterruptedException {
        File inputFile = new File("/home/zengguang/导出.csv");
        // 设置 CSV 文件路径
        double[][] data2D = DataDealUtils.read2ArrayCSV(inputFile);
        TrainModelDl4j trainModel = new TrainModelDl4j(11, 1, Arrays.asList(22, 100,22), 60 * 1000L, 0.999, "/home/zengguang/myModel.zip");
        trainModel.trainModel(data2D);
        for(double[] datax:data2D ){
            System.out.println("---" +  trainModel.doModel(datax));
        }

/*        File inputFile = new File("/home/zengguang/1.csv");
        // 设置 CSV 文件路径
        double[][] data2D = DataDealUtils.read2ArrayCSV(inputFile);
        TrainModelDl4j trainModel = new TrainModelDl4j(11, 3, Arrays.asList(22, 100, 22),60 * 1000L, 0.999, "/home/zengguang/1.zip");
        trainModel.trainModel(data2D);
        for(double[] datax:data2D ){
            System.out.println("---" +  trainModel.doModel(datax));
        }*/


    }

}
