package io.github.java_zengguang.litepress.ml.util;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.tinylog.Logger;

public class OneHotUtil {


    /**
     * 将列为1的特征矩阵进行One-Hot编码，得到列为n的矩阵。
     *
     * @param input 输入矩阵，形状为 [batchSize, 1]
     * @param n     类别总数
     * @return One-Hot编码后的矩阵，形状为 [batchSize, n]
     */
    public static INDArray oneHotEncode(INDArray input, int n) {
        if (n == 1) {
            return input;
        }
        int batchSize = (int) input.shape()[0];
        INDArray oneHotEncoded = Nd4j.zeros(batchSize, n);

        for (int i = 0; i < batchSize; i++) {
            int value = (int) input.getDouble(i); // 假设输入是整数类型
            if (value >= 0 && value < n) {
                oneHotEncoded.putScalar(new int[]{i, value}, 1.0);
            } else {
                Logger.info("Warning: Value index " + value + " is out of bounds for sample " + i);
            }
        }

        return oneHotEncoded;
    }

    /**
     * 将One-Hot编码后的矩阵转换回原来的特征矩阵。
     *
     * @param oneHotEncoded One-Hot编码后的矩阵，形状为 [batchSize, n]
     * @return 原始特征矩阵，形状为 [batchSize, 1]
     */
    public static INDArray oneHotDecode(INDArray oneHotEncoded) {

        if (oneHotEncoded.shape()[1] == 1) {
            return Transforms.round(oneHotEncoded);
        }

        int batchSize = (int) oneHotEncoded.shape()[0];
        INDArray originalFeatures = Nd4j.zeros(batchSize, 1);

        for (int i = 0; i < batchSize; i++) {
            double[] row = oneHotEncoded.getRow(i).toDoubleVector();
            int originalValue = -1;
            for (int j = 0; j < row.length; j++) {
                if (Math.round(row[j]) == 1.0) {
                    originalValue = j;
                    break;
                }
            }
            if (originalValue == -1) {
                Logger.info("Warning: No valid one-hot encoding found for sample " + i);
            } else {
                originalFeatures.putScalar(new int[]{i, 0}, (double) originalValue);
            }
        }

        return originalFeatures;
    }


    /**
     * 将一个数字归一化编码为一个数组。
     *
     * @param value 输入的整数值
     * @param n     类别总数
     * @return 归一化编码后的double数组
     */
    public static double[] normalizeEncode(double value, int n) {

        if (n == 1) {
            double data[] = {value};
            return data;
        }

        n = n - 1;
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative.");
        }
        if (value < 0 || value > n) {
            throw new IllegalArgumentException("Input value " + value + " is out of bounds for n=" + n);
        }

        // 创建长度为 n+1 的数组
        double[] encoded = new double[n + 1];

        // 如果 n 不为0，则进行归一化
        if (n != 0) {
            // 归一化值
            double normalizedValue = (double) value / n;

            // 找到最接近的索引位置
            int index = (int) Math.round(normalizedValue * n);

            // 设置该位置的值为 1.0
            encoded[index] = 1.0;
        } else {
            // 如果 n 为 0，则只有一种可能性，设置第一个元素为 1.0
            encoded[0] = 1.0;
        }

        return encoded;
    }

    /**
     * 将归一化编码后的数组解码回原始数字。
     *
     * @param encoded 归一化编码后的double数组
     * @param n       类别总数
     * @return 解码后的整数值
     */
    public static double normalizeDecode(double[] encoded, int n) {
        if (n == 1) {
            return Math.round(encoded[0]);
        }
        n = n - 1;
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative.");
        }
        if (encoded == null || encoded.length != n + 1) {
            throw new IllegalArgumentException("Encoded array length does not match n.");
        }

        int originalValue = -1;
        for (int i = 0; i < encoded.length; i++) {
            if (Math.round(encoded[i]) == 1.0) {  //double会丢失精度，所有取近似整数
                originalValue = i;
                break;
            }
        }

        if (originalValue == -1) {
            Logger.info("Warning: No valid one-hot encoding found for sample ");
        }

        // 反归一化
        return Math.round(originalValue * ((double) n / n));
    }

}

