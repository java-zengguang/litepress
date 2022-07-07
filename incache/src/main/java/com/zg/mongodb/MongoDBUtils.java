package com.zg.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.zg.common.bean.entity.OptionMGDB;
import com.zg.common.init.Config;
import org.apache.commons.collections.map.HashedMap;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Administrator on 2018/12/12 0012.
 */
public class MongoDBUtils {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBUtils.class.getName());
    private static ThreadLocal<MongoDatabase> threadLocal = new ThreadLocal();
    private static OptionMGDB optionMGDB = (OptionMGDB) Config.getConfig("optionMGDB");

    public static MongoDatabase getConnection() {
        MongoDatabase mongoDatabase = null;
        mongoDatabase = threadLocal.get();
        if (mongoDatabase == null) {
            try {

                ServerAddress serverAddress = new ServerAddress(optionMGDB.ip, Integer.valueOf(optionMGDB.port));
                List<ServerAddress> addrs = new ArrayList<ServerAddress>();
                addrs.add(serverAddress);
                //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
                MongoCredential credential = MongoCredential.createScramSha1Credential(optionMGDB.username, optionMGDB.database, optionMGDB.password.toCharArray());
                List<MongoCredential> credentials = new ArrayList<MongoCredential>();
                credentials.add(credential);
                //通过连接认证获取MongoDB连接
                MongoClient mongoClient = new MongoClient(addrs, credentials);
                mongoDatabase = mongoClient.getDatabase(optionMGDB.database);
                logger.info("Connect to database successfully");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return null;
            }
            return mongoDatabase;
        }
        threadLocal.set(mongoDatabase);
        return mongoDatabase;
    }

    public static Set<String> getCollectionNames() {
        Set<String> resultSet = new HashSet();
        MongoDatabase mongoDatabase = getConnection();
        MongoIterable<String> mi = mongoDatabase.listCollectionNames();
        Iterator it = mi.iterator();
        while (it.hasNext()) {
            resultSet.add((String) it.next());
        }

        return resultSet;
    }

    public static boolean hasCollection(String collectionName) {
        Set<String> collectionNames = getCollectionNames();
        for (String name : collectionNames) {
            if (name.trim().equals(collectionName.trim())) {
                return true;
            }
        }
        return false;
    }


    private static MongoCollection<Document> getMongoCollection(String collectionName) {
        if (!hasCollection(collectionName)) {
            logger.info("未找到" + collectionName);
            return null;
        }
        MongoDatabase mongoDatabase = getConnection();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection;
    }

    private static List<Document> getDocumentList(MongoCollection collection, Bson filter) {
        List<Document> resultList = new ArrayList();
        FindIterable<Document> findIterable = collection.find(filter);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            resultList.add(document);
        }
        return resultList;
    }


    public static List<Document> getDocumentList(String collectionName, Map filter) {
        List list = new ArrayList();
        MongoCollection collection = getMongoCollection(collectionName);
        if (collection == null) {

        } else {
            Bson filte = new BasicDBObject(filter);
            list = MongoUtils.bsonToMapList(getDocumentList(collection, filte));
        }

        return list;
    }

    private static void insertDocumentList(MongoCollection collection, List<Document> list) {
        collection.insertMany(list);
    }

    private static void insertOneMap(String collectionName, Document document) {
        MongoCollection collection = getMongoCollection(collectionName);
        if (collection == null) {
            collection = createCollection(collectionName);
        }
        collection.insertOne(document);

    }

    public static void insertOneMap(String collectionName, Map map) {
        insertOneMap(collectionName, new Document(map));
    }

    public static void insertMapList(String collectionName, List<Map> list) {
        List<Document> documents = new ArrayList<>();
        for (Map map : list) {
            documents.add(new Document(map));
        }
        MongoCollection collection = getMongoCollection(collectionName);
        if (collection == null) {
            collection = createCollection(collectionName);
        }
        insertDocumentList(collection, documents);
    }

    private static MongoCollection<Document> createCollection(String collectionName) {
        MongoDatabase mongoDatabase = getConnection();
        mongoDatabase.createCollection(collectionName);
        return getMongoCollection(collectionName);
    }


    private static void deleteDocument(String collectionName, Bson filter) {
        MongoDatabase mongoDatabase = getConnection();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        collection.deleteMany(filter);
    }

    public static void deleteDocument(String collectionName, Map filter) {
        BasicDBObject basicDBObject = new BasicDBObject(filter);
        deleteDocument(collectionName, (Bson) basicDBObject);
    }

    private static void updateDocument(String collectionName, Bson filter, Document document) {
        MongoDatabase mongoDatabase = getConnection();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        collection.updateMany(filter, new Document("$set", document));
    }

    public static void updateDocument(String collectionName, Map oldData, Map newData) {
        BasicDBObject filter = new BasicDBObject(oldData);
        Document document = new Document(newData);
        updateDocument(collectionName, (Bson) filter, document);
    }

    public static void main(String args[]) {
        Map map = new HashedMap();
    }
}
