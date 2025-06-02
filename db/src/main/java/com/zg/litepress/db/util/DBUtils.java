package com.zg.litepress.db.util;

import com.zg.litepress.core.annotation.Model;

public class DBUtils {

    public static String getTableNameFromModel(Class modelClass) {
        Model model = (Model) modelClass.getAnnotation(Model.class);
        String tableName = null;
        if (model != null) {
            tableName = model.tableName();
            if (tableName == null || tableName.equals("")) {
                tableName = modelClass.getSimpleName();
            }
        }

        return tableName;
    }
}
