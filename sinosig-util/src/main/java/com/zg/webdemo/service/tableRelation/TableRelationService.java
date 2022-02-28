package com.zg.webdemo.service.tableRelation;

import com.zg.webdemo.entity.LDCode;
import com.zg.webdemo.entity.TableRelationShipEntity;

import java.util.List;

public interface TableRelationService {
    void reLoadTableRelationA(List<LDCode> ldCodes) throws Exception;
    void reLoadTableRelation(List<TableRelationShipEntity> tableRelationShipEntities) throws Exception;
}
