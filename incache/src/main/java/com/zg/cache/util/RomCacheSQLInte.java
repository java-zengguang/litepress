package com.zg.cache.util;

public interface RomCacheSQLInte extends RomCacheInte {
    boolean downLoadDatabese() throws Exception;

    boolean upLoadDatabase();

    boolean submit();
}
