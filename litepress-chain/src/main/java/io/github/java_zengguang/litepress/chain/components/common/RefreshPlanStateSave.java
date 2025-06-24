package io.github.java_zengguang.litepress.chain.components.common;

import io.github.java_zengguang.litepress.chain.components.BaseCommonComponent;
import io.github.java_zengguang.litepress.chain.components.Components;
import io.github.java_zengguang.litepress.chain.entity.BaseProcess;
import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.db.dao.database.NewJDBCUtil;
import io.github.java_zengguang.litepress.db.util.ModelSQLUtils;


import java.util.ArrayList;
import java.util.List;

@Components(name = "RefreshPlanStateSave", type = "java")

public class RefreshPlanStateSave extends BaseCommonComponent {

    NewJDBCUtil newJDBCUtil = new NewJDBCUtil("optionDB");

    @Override
    public BaseProcess doExecute(BaseProcess baseProcess) throws Exception {
        List<String> sqlList = new ArrayList<>();
        OptionDB optionDB = (OptionDB) Config.getConfig("optionDB");
        if ("0".equals(baseProcess.executestate)) {
            String sql = ModelSQLUtils.insert(baseProcess, optionDB.dbtype);
            sqlList.add(sql);

        } else {
            String sql = ModelSQLUtils.updateByPK(baseProcess, optionDB.dbtype);
            sqlList.add(sql);

        }
        newJDBCUtil.batchSql(sqlList, true);

        return baseProcess;
    }
}
