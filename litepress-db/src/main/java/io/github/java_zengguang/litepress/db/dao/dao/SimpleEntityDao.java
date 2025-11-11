package io.github.java_zengguang.litepress.db.dao.dao;

import com.github.pagehelper.PageInfo;
import io.github.java_zengguang.litepress.core.bean.entity.PageEntity;
import io.github.java_zengguang.litepress.db.dao.manager.TransactionManager;

import java.io.File;
import java.util.List;
import java.util.Map;


public class SimpleEntityDao<T> implements EntityDao<T> {
    private final String dataSource;
    private final BaseEntityDao<T> baseEntityDao;
    private final TransactionManager transactionManager;

    public SimpleEntityDao(String dataSource) {
        this.dataSource = dataSource;
        this.baseEntityDao = new BaseEntityDao<>(dataSource);
        this.transactionManager=TransactionManager.getInstance();

    }


    public void manualCommitClose() throws Exception {
        transactionManager.commit();
        transactionManager.release(dataSource);
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


    public int insertTable(T model) throws Exception {
        try {
            return baseEntityDao.insertTable(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }

    public Integer operation(String sql) throws Exception {

        try {
            return baseEntityDao.operation(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }


    }

    public T insertAutoIncrease(T model) throws Exception {
        try {
            return baseEntityDao.insertAutoIncrease(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public int[] insertTables(List<T> modelLIst, Class<T> modelClass) throws Exception {
        try {
            return baseEntityDao.insertTables(modelLIst, modelClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }

    @Override
    public int[] batchSQL(List<String> sqlList) throws Exception {
        try {
            return baseEntityDao.batchSQL(sqlList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public int[] insertTables(List<T> modelList, String tableName) throws Exception {
        try {
            return baseEntityDao.insertTables(modelList, tableName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }


    }


    public List<T> select(String sql, String tableName) throws Exception {
        try {
            return baseEntityDao.select(sql, tableName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public String selectOneValue(String sql) throws Exception {
        try {
            return baseEntityDao.selectOneValue(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<T> select(String sql) throws Exception {
        try {
            return baseEntityDao.select(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<Map> selectToMapList(String sql) throws Exception {
        try {
            return baseEntityDao.selectToMapList(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public Class<?> selectStream(String sql, String tableName, String tempFileDir, List<File> tempFileList, Integer fileSize) throws Exception {
        try {
            return baseEntityDao.selectStream(sql, tableName, tempFileDir, tempFileList, fileSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }
    }


    public Class<?> selectStream(String sql, File tempFile) throws Exception {
        try {
            return baseEntityDao.selectStream(sql, tempFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<String> selectOneColList(String sql) throws Exception {
        try {
            return baseEntityDao.selectOneColList(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<T> select(String sql, Class<T> modelClass) throws Exception {
        try {
            return baseEntityDao.select(sql, modelClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<T> select2Page(String sql, Class<T> tClass, PageEntity page) throws Exception {
        try {
            return baseEntityDao.select2Page(sql, tClass, page);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public List<T> execute(String sql) throws Exception {
        try {
            return baseEntityDao.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }
    }


    public int updateModel(Object object, String... terms) throws Exception {
        try {
            return baseEntityDao.updateModel(object, terms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }


    public Integer insertMap2Data(String tableName, Map<String, String> para) throws Exception {
        try {
            return baseEntityDao.insertMap2Data(tableName, para);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.manualCommitClose();
        }

    }
}
