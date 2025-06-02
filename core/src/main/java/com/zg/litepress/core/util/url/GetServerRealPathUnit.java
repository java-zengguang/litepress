package com.zg.litepress.core.util.url;

import java.io.File;
import java.io.FileNotFoundException;


public class GetServerRealPathUnit {

    public static String getPath(String subdirectory) {
        File upload = null;
        try {
            File path = new File(ResourceUtils.getURL("/").getPath());
            if (!path.exists()) path = new File("");
            upload = new File(path.getAbsolutePath(), subdirectory);
            if (!upload.exists()) upload.mkdirs();
            return upload + "/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException("路径错误");
        }
    }
}