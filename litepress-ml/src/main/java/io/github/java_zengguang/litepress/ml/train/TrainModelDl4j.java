package io.github.java_zengguang.litepress.ml.train;

import io.github.java_zengguang.litepress.ml.util.DataDealUtils;
import io.github.java_zengguang.litepress.ml.util.OneHotUtil;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

public class TrainModelDl4j extends BaseTrainModel implements TrainModel {

    private MultiLayerNetwork network;
    /**
     * 特征归一化器(z-score)。仅在训练特征上拟合,随模型一起持久化到 {@code <localFilePath>.norm},
     * 推理时对原始输入做同一套变换,保证训练/推理量纲一致。标签列不归一化。
     */
    private NormalizerStandardize normalizer;


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
        File normFile = new File(localFilePath + ".norm");
        if (file.exists()) {
            Logger.info("加载模型");
            network = readToLocation(file);
            normalizer = loadNormalizer(normFile);
        } else if (hiddenLayer != null) {
            //显式指定了网络结构,按配置建网
            Logger.info("加载模型");
            network = createNetwork();
            network.init();
        }
        //hiddenLayer 为空且无已存模型:延迟到 trainModel 时按样本量自动构建,这里不建网
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
        //延迟建网:hiddenLayer 未指定时,按样本量自动选择网络规模
        if (network == null) {
            this.hiddenLayer = autoHiddenLayers(trainData.length, input);
            Logger.info("自动构建网络 样本数=" + trainData.length + " hiddenLayer=" + hiddenLayer);
            network = createNetwork();
            network.init();
        }

        INDArray allData = Nd4j.create(trainData);
        // 使用 getColumns 方法来选择特定的列
        INDArray features = allData.getColumns(IntStream.rangeClosed(0, input - 1).toArray()); // 获取前 n 列作为特征
        INDArray labels = allData.getColumns(IntStream.rangeClosed(input, allData.columns() - 1).toArray()); // 获取从第 n 列到最后一列作为标签
        labels = OneHotUtil.oneHotEncode(labels, output); //归一化编码

        // 创建 DataSet
        DataSet dataSet = new DataSet(features, labels);

        //归一化:在训练集特征上拟合 z-score,原地 transform;标签列不受影响
        this.normalizer = new NormalizerStandardize();
        this.normalizer.fit(dataSet);
        this.normalizer.transform(dataSet);

        //取出归一化后的全量特征/标签,每轮 dup+打乱+切分一次
        //(原实现每轮调两次 splitTestAndTrain,该 API 会原地修改 DataSet,二次调用操作的是已缩小的数据,
        // 且 80/20 固定切分不随轮变化,导致训练退化)
        INDArray fullFeatures = dataSet.getFeatures();
        INDArray fullLabels = dataSet.getLabels();
        int n = (int) fullFeatures.size(0);

        // 创建 Evaluation 对象
        Evaluation eval = new Evaluation(2); // 假设二分类任务
        Date beginDate = new Date();
        do {
            // 简单的训练/测试集划分 (80%/20%):每轮打乱后单次切分
            int splitIndex = (int) (n * 0.8);
            DataSet full = new DataSet(fullFeatures.dup(), fullLabels.dup());
            full.shuffle();
            var split = full.splitTestAndTrain(splitIndex);
            DataSet trainDataSet = split.getTrain();
            DataSet testDataSet = split.getTest();
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
        saveNormalizer(new File(localFilePath + ".norm"));
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
        //模型尚未训练(network 延迟构建):返回 -2 表示模型缺失,由调用方识别
        if (network == null) {
            return -2;
        }
        // 从文件加载模型
        inputData = Arrays.stream(inputData).limit(input).toArray();
        INDArray indArray = Nd4j.create(inputData).reshape(1, inputData.length);
        //推理用训练时同一套归一化器变换原始输入
        if (normalizer != null) {
            normalizer.transform(indArray);
        }
        INDArray outArray = network.output(indArray, false);
        outArray= OneHotUtil.oneHotDecode(outArray);
        return outArray.toDoubleVector()[0];
    }

    @Override
    public void clearModel() {
        File file=new File(localFilePath);
        file.deleteOnExit();
        new File(localFilePath + ".norm").deleteOnExit();
    }

    /**
     * 按样本量级自动选择隐藏层结构(best-practice 启发式),每层宽度 clamp 到 [8,256]。
     * <ul>
     *   <li>n &gt;= 2000:[2*input, 2*input, input] —— 样本充足,两层隐藏层</li>
     *   <li>500 &lt;= n &lt; 2000:[2*input, input]</li>
     *   <li>n &lt; 500:[input] —— 单隐藏层,避免过拟合</li>
     * </ul>
     */
    static List<Integer> autoHiddenLayers(int n, int input) {
        int h2 = clamp(2 * input);
        int h1 = clamp(input);
        if (n >= 2000) {
            return List.of(h2, h2, h1);
        }
        if (n >= 500) {
            return List.of(h2, h1);
        }
        return List.of(h1);
    }

    private static int clamp(int v) {
        return Math.max(8, Math.min(256, v));
    }

    private void saveNormalizer(File normFile) throws IOException {
        if (normalizer == null) {
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(normFile))) {
            oos.writeObject(normalizer);
        }
    }

    private NormalizerStandardize loadNormalizer(File normFile) {
        if (!normFile.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(normFile))) {
            return (NormalizerStandardize) ois.readObject();
        } catch (Exception e) {
            Logger.error(e, "归一化器加载失败: " + normFile.getAbsolutePath());
            return null;
        }
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
