package com.zg.common.dao.filter;

import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import org.tinylog.Logger;

import java.util.List;
@AutoLoad
public  class DMLFilter extends FilterEventAdapter {



    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
       List<String> sqlList= statement.getBatchSqlList();
       Logger.info("操作脚本"+sqlList);
    }


}
