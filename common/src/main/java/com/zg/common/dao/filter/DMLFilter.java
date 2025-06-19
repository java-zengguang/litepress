package com.zg.common.dao.filter;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

import java.util.List;

public  class DMLFilter extends FilterEventAdapter {



    protected void statementPrepareCallAfter(CallableStatementProxy statement) {
       List<String> sqlList= statement.getBatchSqlList();
       System.out.println("操作脚本"+sqlList);
    }


}
