package io.github.java_zengguang.litepress.db.dao.database;

import io.github.java_zengguang.litepress.core.annotation.AutoIncrease;
import io.github.java_zengguang.litepress.core.bean.entity.MainModel;
import io.github.java_zengguang.litepress.core.bean.entity.MetadataEntity;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.db.dao.assemble.SimpleAssemble;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.core.relect.dynameic.DynamicClass;
import io.github.java_zengguang.litepress.db.util.DBUtils;
import io.github.java_zengguang.litepress.db.util.ModelSQLUtils;
import net.sf.jsqlparser.JSQLParserException;
import org.tinylog.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class BaseEntityDao extends BaseJDBCDao {


    public int insertTable(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        List list = new ArrayList();
        list.add(model);
        int[] results = insertTables(list, model.getClass());
        int result = 0;
        if (results != null && results.length > 0) {
            result = results[0];
        }
        return result;
    }

    public Object insertAutoIncrease(Object model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class clazz = model.getClass();
        Field[] fields = clazz.getFields();
        Field idField = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(AutoIncrease.class)).findFirst().get();
        if (insertTable(model) > 0) {
            String sql = "select @@IDENTITY as id ";
            List<Map> list = selectToMapList(sql);
            Map<String, BigInteger> map = list.get(0);
            Logger.info("id=" + map.get("id").intValue());
            Integer id = map.get("id").intValue();
            idField.set(model, id);
        }
        return model;
    }

    public int[] insertTables(List modelLIst, Class modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        String tableName = DBUtils.getTableNameFromModel(modelClass);
        return insertTables(modelLIst, modelClass, tableName);
    }


    //查询
    public List select(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException {
        List<List<MetadataEntity>> templeList = select2TempleList(sql);
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.dbtype);
        List modelList = new ArrayList();
        Class modelClass = null;
        if (templeList != null && templeList.size() > 0) {
            modelClass = DynamicClass.getDynamicModel(templeList.get(0));
            for (List<MetadataEntity> columnList : templeList) {
                Object obj = modelClass.newInstance();
                for (MetadataEntity metadataEntity : columnList) {
                    obj = simpleAssemble.assembling(metadataEntity, obj);
                }
                modelList.add(obj);
            }
        }
        return modelList;
    }


    //查询
    public List select(String sql, Class modelClass) throws Exception {
        List<List<MetadataEntity>> templeList = null;
        String tableName = DBUtils.getTableNameFromModel(modelClass);
        if (tableName != null) {
            templeList = select2TempleList(sql, tableName);
        } else {
            templeList = select2TempleList(sql);
        }

        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        SimpleAssemble simpleAssemble = new SimpleAssemble(optionDB.dbtype);
        List modelList = new ArrayList();
        for (List<MetadataEntity> columnList : templeList) {
            Object obj = modelClass.newInstance();
            for (MetadataEntity metadataEntity : columnList) {
                obj = simpleAssemble.assembling(metadataEntity, obj);
            }
            modelList.add(obj);
        }
        return modelList;

    }


    public List select2Page(String sql, Class tClass, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        List contMapList = this.selectToMapList(countSql);
        Map map = (Map) contMapList.get(0);
        Integer totalResultSize = Math.toIntExact((Long) map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize((totalResultSize / page.getPageSize()));
        Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
        /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
        sql = sql + " limit " + startRows + " , " + page.getPageSize();
        return select(sql, tClass);
    }

    public List execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException {
        Logger.debug(sql);
        List<MainModel> list = new ArrayList<MainModel>();
        if (sql == null) {
            Logger.debug(" execute  sql is null");
        } else if (sql.startsWith("select")) {
            list = select(sql);

        } else if (operation(sql) > 0) {


        }
        return list;
    }


    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        int result = 0;
        OptionDB optionDB = (OptionDB) Config.getConfig(dataSource);
        if (terms != null && terms.length > 0) {
            String sql = ModelSQLUtils.update(optionDB.dbtype, object, terms);
            result = operation(sql);
        }
        return result;
    }


    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception {
        String sql = "insert into " + tableName;
        String column = "";
        String values = "";
        Set<String> columnSet = para.keySet();
        for (String c : columnSet) {
            column = c + " ," + column;
            values = para.get(c) + " ," + "'" + values + "'";
        }
        if (column.endsWith(",")) {
            column = column.substring(0, column.length() - 1);
        }
        if (values.endsWith(",")) {
            values = values.substring(0, values.length() - 1);
        }
        sql = sql + " (" + column + ")" + " values (" + values + ")";
        return operation(sql);

    }


}
