package com.zg.util.dbcache;

import com.zg.database.dbcache.ROMCache;
import com.zg.util.reflect.JsonUtils;
import com.zg.util.test.Test;

import java.util.List;

/**
 * Created by Administrator on 2018/12/13 0013.
 */
public class DBCacheTest {
    private static ROMCache romCache=new ROMCache(new Test());

    static {
        try {
            romCache.downLoadDatabese(Test.class,"select *from test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {

        // romCache.deleteModel("id=1");
        List list=romCache.getList();
         System.out.println(JsonUtils.objectToJson(list));
       /*  romCache.upLoadDatabase();
         romCache.submit();*/
    }
}
