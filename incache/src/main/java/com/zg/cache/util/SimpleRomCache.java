package com.zg.cache.util;

import com.zg.database.util.JDBCUtils;

public class SimpleRomCache extends BaseRomCache {
    public SimpleRomCache(Class modelClass, String sql) {
        super(modelClass,sql);
    }


}
