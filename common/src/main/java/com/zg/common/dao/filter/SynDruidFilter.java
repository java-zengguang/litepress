package com.zg.common.dao.filter;

import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

import java.util.List;
import java.util.logging.Logger;

@AutoLoad
public class SynDruidFilter extends FilterEventAdapter {
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        Logger.getLogger("打印SQL语句：" + sql);

    }

    protected void statementPrepareAfter(PreparedStatementProxy statement) {

        String sql = statement.getSql();
        Logger.getLogger("ps打印SQL语句：" + sql);


    }

    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {

        List<String> sqlList = statement.getBatchSqlList();

    }

}
