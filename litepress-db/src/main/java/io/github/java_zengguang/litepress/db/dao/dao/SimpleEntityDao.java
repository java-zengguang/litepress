package io.github.java_zengguang.litepress.db.dao.dao;

import com.github.pagehelper.PageInfo;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.db.dao.manager.TransactionManager;
import net.sf.jsqlparser.JSQLParserException;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


public class SimpleEntityDao<T> implements EntityDao<T> {
    private final String dataSource;
    private final BaseEntityDao<T> baseEntityDao;

    public SimpleEntityDao(String dataSource) {
        this.dataSource = dataSource;
        this.baseEntityDao = new BaseEntityDao<>(dataSource);
    }


    public void manualCommitClose() throws SQLException, ClassNotFoundException {
        TransactionManager.commit();
        TransactionManager.release(dataSource);
    }

    public String addPageFromSql(String sql, PageEntity page) throws Exception {
        String countSql = null;
        if (sql != null) {
            countSql = "select count(1) as totalResultSize  from ( " + sql + " ) as num";
        }
        List list = this.selectToMapList(countSql);
        Map map = (Map) list.get(0);
        Integer totalResultSize = Integer.valueOf((String) map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize(totalResultSize / page.getPageSize());
        Integer startRows = (page.getCurrentPage() - 1) * page.getPageSize();
        sql = sql + " limit " + startRows + " , " + page.getPageSize();
        return sql;
    }

    public void convertPage(List<T> listT, PageEntity page) {
        PageInfo<T> resultPage = new PageInfo<T>(listT);
        page.setTotalResultSize(Long.valueOf(resultPage.getTotal()).intValue());
        page.setTotalPageSize(resultPage.getPages());
    }


    public int insertTable(T model) throws SQLException, ClassNotFoundException {
        try {
            return baseEntityDao.insertTable(model);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return -1;
    }

    public Integer operation(String sql) throws SQLException, ClassNotFoundException {

        try {
            return baseEntityDao.operation(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return -1;

    }

    public T insertAutoIncrease(T model) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        try {
            return baseEntityDao.insertAutoIncrease(model);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return null;
    }


    public int[] insertTables(List<T> modelLIst, Class<T> modelClass) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        try {
            return baseEntityDao.insertTables(modelLIst, modelClass);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }

        return new int[0];
    }

    @Override
    public int[] batchSQL(List<String> sqlList) throws SQLException, ClassNotFoundException {
        return baseEntityDao.batchSQL(sqlList);
    }


    public int[] insertTables(List<T> modelList, String tableName) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        try {
            return baseEntityDao.insertTables(modelList, tableName);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }

        return new int[0];
    }


    public List<T> select(String sql, String tableName) throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        try {
            return baseEntityDao.select(sql, tableName);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public String selectOneValue(String sql) throws SQLException, ClassNotFoundException {
        try {
            return baseEntityDao.selectOneValue(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return "";
    }


    public List<T> select(String sql) throws SQLException, IllegalAccessException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException, NoSuchMethodException, InvocationTargetException {
        try {
            return baseEntityDao.select(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public List<Map> selectToMapList(String sql) throws SQLException, ClassNotFoundException {
        try {
            return baseEntityDao.selectToMapList(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        try {
            return baseEntityDao.selectStream(sql, tableName, tempFileDir, tempFileList, fileSize);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return null;
    }


    public Class<?> selectStream(String sql, File tempFile) throws SQLException, ClassNotFoundException, IOException, IllegalAccessException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        try {
            return baseEntityDao.selectStream(sql, tempFile);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return null;
    }


    public List<String> selectOneColList(String sql) throws SQLException, ClassNotFoundException {
        try {
            return baseEntityDao.selectOneColList(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public List<T> select(String sql, Class<T> modelClass) throws Exception {
        try {
            return baseEntityDao.select(sql, modelClass);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception {
        try {
            return baseEntityDao.select2Page(sql, tClass, page);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public List<T> execute(String sql) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, ParseException, InstantiationException, JSQLParserException, InvocationTargetException, NoSuchMethodException {
        try {
            return baseEntityDao.execute(sql);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return List.of();
    }


    public int updateModel(Object object, String... terms) throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        try {
            return baseEntityDao.updateModel(object, terms);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return 0;
    }


    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception {
        try {
            return baseEntityDao.insertMap2Data(tableName, para);
        } catch (Exception e) {
            Logger.error(e);
        } finally {
            this.manualCommitClose();
        }
        return 0;
    }
}
