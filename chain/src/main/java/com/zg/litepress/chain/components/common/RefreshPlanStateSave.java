package com.zg.litepress.chain.components.common;

import com.zg.litepress.chain.components.BaseCommonComponent;
import com.zg.litepress.chain.components.Components;
import com.zg.litepress.chain.entity.BaseProcess;
import com.zg.litepress.core.bean.entity.OptionDB;
import com.zg.litepress.core.init.Config;
import com.zg.litepress.db.dao.database.NewJDBCUtil;
import com.zg.litepress.db.util.ModelSQLUtils;


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
