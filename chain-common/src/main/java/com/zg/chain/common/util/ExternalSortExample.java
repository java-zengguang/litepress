package com.zg.chain.common.util;

import java.io.*;
import java.util.*;

public class ExternalSortExample {
    public static void main(String[] args) throws IOException {
        // 创建一个测试用的文件
        createTestFile("input.txt", 100000000);

        // 对文件进行外部排序
        externalSort("input.txt", "output.txt", 10000000);

        // 验证排序结果
        validateSortedFile("output.txt");
    }

    // 创建一个测试用的文件
    public static void createTestFile(String filename, int numValues) throws IOException {
        Random rand = new Random();
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        for (int i = 0; i < numValues; i++) {
            writer.write(String.valueOf(rand.nextInt()));
            writer.newLine();
        }

        writer.close();
    }

    // 对文件进行外部排序
    public static void externalSort(String inputFilename, String outputFilename, int bufferSize) throws IOException {
        // 创建一个临时文件来存储排序后的结果
        File tempFile = File.createTempFile("temp", null);

        // 打开输入文件和临时文件的读写流
        BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        ArrayList<Integer> values = new ArrayList<>(bufferSize);

        String line;
        while ((line = reader.readLine()) != null) {
            values.add(Integer.parseInt(line));

            // 当临时数据列表的大小达到缓冲区大小时，对其进行排序，并写入临时文件
            if (values.size() >= bufferSize) {
                Collections.sort(values);

                for (Integer value : values) {
                    writer.write(String.valueOf(value));
                    writer.newLine();
                }

                values.clear();
            }
        }

        // 对剩余的数据进行排序，并写入临时文件
        if (!values.isEmpty()) {
            Collections.sort(values);

            for (Integer value : values) {
                writer.write(String.valueOf(value));
                writer.newLine();
            }

            values.clear();
        }

        // 关闭输入文件和临时文件的读写流
        reader.close();
        writer.close();

        // 对临时文件进行归并排序，将结果输出到最终的输出文件
        mergeSort(tempFile, outputFilename, bufferSize);

        // 删除临时文件
        tempFile.delete();
    }

    // 对临时文件进行归并排序
    public static void mergeSort(File input, String outputFilename, int bufferSize) throws IOException {
        // 打开输入文件和输出文件的读写流
        BufferedReader reader = new BufferedReader(new FileReader(input));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));

        // 创建一个优先队列，用于进行归并排序
        PriorityQueue<String> pq = new PriorityQueue<>();

        // 创建一个缓冲区，用于减少I/O操作
        ArrayList<String> buffer = new ArrayList<>(bufferSize);

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.add(line);

            // 当缓冲区的大小达到缓冲区大小时，将其排序后加入到优先队列中
            if (buffer.size() >= bufferSize) {
                Collections.sort(buffer);

                for (String value : buffer) {
                    pq.add(value);
                }

                buffer.clear();
            }

            // 当优先队列的大小达到缓冲区大小时，将其排序后写入输出文件
            if (pq.size() >= bufferSize) {
                while (!pq.isEmpty()) {
                    writer.write(pq.poll());
                    writer.newLine();
                }
            }
        }

        // 对剩余的数据进行排序，并写入输出文件
        for (String value : buffer) {
            pq.add(value);
        }

        while (!pq.isEmpty()) {
            writer.write(pq.poll());
            writer.newLine();
        }

        // 关闭输入文件和输出文件的读写流
        reader.close();
        writer.close();
    }

    // 验证排序结果
    public static void validateSortedFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String line;
        int previousValue = Integer.MIN_VALUE;
        while ((line = reader.readLine()) != null) {
            int currentValue = Integer.parseInt(line);

            if (currentValue < previousValue) {
                System.out.println("文件未被正确排序！");
                return;
            }

            previousValue = currentValue;
        }

        reader.close();
        System.out.println("文件已被正确排序！");
    }
}