package io.github.java_zengguang.litepress.ml.util;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataDealUtils {

    public static double[][] read2ArrayCSV(File inputFile) throws IOException, InterruptedException {
        // 创建 CSV 记录阅读器，跳过第一行（标题）
        int skipLines = 1; // 如果有标题行，则设置为1
        RecordReader recordReader = new CSVRecordReader(skipLines, ',');
        recordReader.initialize(new FileSplit(inputFile));

        // 将记录读入 List 中
        List<double[]> dataList = new ArrayList<>();
        while (recordReader.hasNext()) {
            List<Writable> record = recordReader.next();
            double[] row = record.stream().mapToDouble((x) -> x.toDouble()).toArray();
            dataList.add(row);
        }
        return dataList.toArray(new double[0][]);
    }
    public static DataSetIterator readCSV(File inputFile,Integer batchSize) throws IOException, InterruptedException {
        // 创建 CSV 记录阅读器，跳过第一行（标题）
        CSVRecordReader recordReader = new CSVRecordReader();
        recordReader.initialize(new FileSplit(inputFile));
        // 创建 RecordReaderDataSetIterator 并应用 TransformProcess
        DataSetIterator iterator = new RecordReaderDataSetIterator.Builder(recordReader, batchSize)
                .build();
        return iterator;
    }


}
