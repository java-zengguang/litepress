package com.zg.webdemo.service.tableRelation;

import com.zg.webdemo.entity.LDCode;

import java.util.List;

public interface TableRelationService {
    void reLoadTableRelation(List<LDCode> ldCodes) throws Exception;
}
