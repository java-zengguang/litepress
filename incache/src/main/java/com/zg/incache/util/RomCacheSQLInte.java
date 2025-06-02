package com.zg.incache.util;

import java.sql.SQLException;

public interface RomCacheSQLInte extends RomCacheInte {
    boolean downLoadDatabese() throws Exception;

    boolean upLoadDatabase();

    boolean submit() throws SQLException, ClassNotFoundException;
}
