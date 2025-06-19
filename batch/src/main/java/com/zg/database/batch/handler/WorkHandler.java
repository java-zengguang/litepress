package com.zg.database.batch.handler;

import java.util.List;

public interface WorkHandler {
     Object doHandler(List<?> dataList);
}
