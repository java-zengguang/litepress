package com.zg.chain.common.components.common;

import com.zg.chain.common.entity.BaseProcess;
import com.zg.common.bean.entity.OptionDB;
import com.zg.common.dao.database.NewJDBCUtil;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.ModelSQLUtils;
import com.zg.chain.common.components.BaseCommonComponent;
import com.zg.chain.common.components.Components;

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
            String sql = ModelSQLUtils.insert(baseProcess, optionDB.DBType);
            sqlList.add(sql);

        } else {
            String sql = ModelSQLUtils.updateByPK(baseProcess, optionDB.DBType);
            sqlList.add(sql);

        }
        newJDBCUtil.batchSql(sqlList, true);

        return baseProcess;
    }
}
