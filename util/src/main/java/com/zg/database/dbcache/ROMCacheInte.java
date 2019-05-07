package com.zg.database.dbcache;

import java.util.List;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public interface ROMCacheInte {

    List getList();

    boolean updateList(Object model, String... terms);

    boolean updateListIndex(Object model, int index);

    boolean appendList(List list);

    boolean deleteModel(String... terms);

    boolean deleteModel(int index);

    boolean toOrder(String... terms);

    boolean toSimple(String primayKey);

    List findModel(String... terms);

    boolean downLoadDatabese(Class modelClass, String sql) throws Exception;

    boolean upLoadDatabase();

    boolean submit();
}
