package com.zg.database;


import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Test1 {

    private static List<String> tokenizeChineseText(String text) {
        List<String> result = new ArrayList<>();
        try (StringReader reader = new StringReader(text)) {
            IKSegmenter ikSegmenter = new IKSegmenter(reader, true); // true表示使用智能分词模式
            Lexeme lexeme;
            while ((lexeme = ikSegmenter.next()) != null) {
                result.add(lexeme.getLexemeText());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during tokenization", e);
        }
        return result;
    }

    public static INDArray averageWordVectors(Word2Vec model, String sentence) {
       // TokenizerFactory tFactory = new DefaultTokenizerFactory();
        List<String> tokens = tokenizeChineseText(sentence);

        if (tokens.isEmpty()) {
            return null; // 或者返回零向量
        }

        int vectorSize = model.lookupTable().layerSize();
        INDArray sum = Nd4j.zeros(vectorSize);

        for (String token : tokens) {
            System.out.println(token);
            if (model.hasWord(token)) {
                sum.addi(model.getWordVectorMatrix(token));
            }
        }

        return sum.div(tokens.size());
    }
    public static void main(String[] args) throws IOException {
        // 加载FastText模型

        List<String> tokens =  tokenizeChineseText("告警对象：URL响应时效告警");
        tokens.forEach(System.out::println);
        File fastTextModelFile = new File("/home/zengguang/文档/cc.zh.300.vec");
        Word2Vec model = WordVectorSerializer.readWord2VecModel(fastTextModelFile);
        // 使用模型进行查询
        System.out.println(model.wordsNearest("你好", 5));
        System.out.println(averageWordVectors(model,"告警对象：URL响应时效告警"));

        System.out.println(averageWordVectors(model,"接下来，我们需要将文本转换为向量表示。可以使用预训练的Doc2Vec模型或者训练自己的文档向量化模型。这里我们以训练自己的Doc2Vec模型为例："));


    }
}
