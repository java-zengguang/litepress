package io.github.java_zengguang.litepress.ml.train;

import io.github.java_zengguang.litepress.ml.util.DataDealUtils;
import io.github.java_zengguang.litepress.ml.util.OneHotUtil;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.propagation.Propagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrainModelEncog extends BaseTrainModel implements TrainModel {

    private BasicNetwork network;

    public TrainModelEncog(Integer input, Integer output, List<Integer> hiddenLayer, Long timeOut, Double accuracyRate, String localFilePath) throws IOException {
        this.input = input;
        this.output = output;
        this.localFilePath = localFilePath;
        this.hiddenLayer = hiddenLayer;
        this.timeOut = timeOut;
        this.accuracyRate = accuracyRate;
        initModel();
    }


    private BasicNetwork readToLocation(File modelFile) throws IOException {
        return (BasicNetwork) EncogDirectoryPersistence.loadObject(modelFile);
    }

    private void locationToSave(File file) throws IOException {
        EncogDirectoryPersistence.saveObject(file, this.network);
    }


    private BasicNetwork createNetwork() {
        Logger.info("未找到模型，重新创建！");
        FeedForwardPattern pattern = new FeedForwardPattern();
        pattern.setInputNeurons(input);
        pattern.setOutputNeurons(output);
        pattern.setActivationFunction(new ActivationSigmoid());
        hiddenLayer.forEach(x -> {
            pattern.addHiddenLayer(x);
        });
        BasicNetwork network = (BasicNetwork) pattern.generate();
        network.reset();
        this.network = network;
        return this.network;
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

    }


    @Override
    public double trainModel(double[][] trainData) throws IOException {
        MLDataSet trainingSet = new BasicMLDataSet();
        for (double[] rowData : trainData) {
            MLData input = new BasicMLData(Arrays.copyOfRange(rowData, 0, super.input));
            MLData ideal = new BasicMLData(OneHotUtil.normalizeEncode(rowData[rowData.length-1], output)); //最后一列为特征，并根据输出神经元数，进行归一化编码
            MLDataPair pair = new BasicMLDataPair(input, ideal);
            trainingSet.add(pair);
        }

        final Propagation train = new ResilientPropagation(network, trainingSet);
        train.setThreadCount(1);
        final Date beginDate = new Date();
        Logger.info("Beginning training...");
        do {
            train.iteration();
            Logger.info(" Accuracy:" +(1 - train.getError()));
        } while ((train.getError() > (1 - accuracyRate)) && !train.isTrainingDone() && (new Date().getTime() - beginDate.getTime() < timeOut));
        train.finishTraining();
        locationToSave(new File(localFilePath));
        return 1-train.getError();
    }

    @Override
    public double doModel(double[] inputData) throws IOException {
        MLData inputMLData = new BasicMLData(Arrays.copyOfRange(inputData, 0, super.input));
        MLData outputMLData=network.compute(inputMLData);
        return OneHotUtil.normalizeDecode(outputMLData.getData(),output);
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
        TrainModelEncog trainModel = new TrainModelEncog(11, 1, Arrays.asList(22, 100,22), 60 * 1000L, 0.999, "/home/zengguang/1.zip");
        trainModel.trainModel(data2D);
        for(double[] datax:data2D ){
            System.out.println("---" +  trainModel.doModel(datax));
        }

/*
        File inputFile = new File("/home/zengguang/1.csv");
        // 设置 CSV 文件路径
        double[][] data2D = DataDealUtils.read2ArrayCSV(inputFile);
        TrainModel trainModel = new TrainModelEncog(11, 3, Arrays.asList(22, 100, 22),60 * 1000L, 0.999, "/home/zengguang/x");
        trainModel.trainModel(data2D);
        for(double[] datax:data2D ){
            System.out.println("---" +  trainModel.doModel(datax));
        }
*/


    }
}
