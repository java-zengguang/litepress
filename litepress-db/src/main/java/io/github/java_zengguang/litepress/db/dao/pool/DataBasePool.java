package io.github.java_zengguang.litepress.db.dao.pool;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import javax.sql.DataSource;


public interface DataBasePool {

    DataSource createDataSource(OptionDB optionDB) throws Exception;

}
