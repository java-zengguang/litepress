package com.zg.search.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


/**
 * Lucene 创建索引
 *
 * @author moonxy
 */
public class BaseIndexHelper {

    public static void main(String[] args) {


        // 开始时间
        Date start = new Date();
        System.out.println("**********开始创建索引**********");
        // 创建IK分词器
        Analyzer analyzer = new StandardAnalyzer();//使用IK最细粒度分词
        IndexWriterConfig icw = new IndexWriterConfig(analyzer);
        // CREATE 表示先清空索引再重新创建
        icw.setOpenMode(OpenMode.CREATE);
        Directory dir = null;
        IndexWriter inWriter = null;
        // 存储索引的目录
        Path indexPath = Paths.get("D:", "test", "index");

        try {
            if (!Files.isReadable(indexPath)) {
                System.out.println("索引目录 '" + indexPath.toAbsolutePath() + "' 不存在或者不可读,请检查");
                System.exit(1);
            }
            dir = FSDirectory.open(indexPath);
            inWriter = new IndexWriter(dir, icw);

            // 设置新闻ID索引并存储
            FieldType idType = new FieldType();
            idType.setIndexOptions(IndexOptions.DOCS);
            idType.setStored(true);

            // 设置新闻标题索引文档、词项频率、位移信息和偏移量，存储并词条化
            FieldType titleType = new FieldType();
            titleType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            titleType.setStored(true);
            titleType.setTokenized(true);

            FieldType contentType = new FieldType();
            contentType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            contentType.setStored(true);
            contentType.setTokenized(true);
            contentType.setStoreTermVectors(true);
            contentType.setStoreTermVectorPositions(true);
            contentType.setStoreTermVectorOffsets(true);

            Document doc1 = new Document();
            doc1.add(new Field("id", String.valueOf(1), idType));
            doc1.add(new Field("title", "dfs df", titleType));
            doc1.add(new Field("content", "sdfhfjbff", contentType));
            doc1.add(new IntPoint("reply", 1));
            doc1.add(new StoredField("reply_display", "1"));


            inWriter.addDocument(doc1);


            inWriter.commit();

            inWriter.close();
            dir.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Date end = new Date();
        System.out.println("索引文档用时:" + (end.getTime() - start.getTime()) + " milliseconds");
        System.out.println("**********索引创建完成**********");
    }
}