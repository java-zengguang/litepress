package com.zg.webdemo.service.tableRelation;

import com.zg.handler.CommitClassHandler;
import com.zg.webdemo.dao.LDCodeMapper;
import com.zg.webdemo.dao.TableRelationMapper;
import com.zg.webdemo.entity.LDCode;
import com.zg.webdemo.entity.TableRelationShipEntity;

import java.util.List;

public class TableRelationServiceImpl extends CommitClassHandler implements TableRelationService {

    LDCodeMapper ldCodeMapper=new LDCodeMapper();

    TableRelationMapper tableRelationMapper=new TableRelationMapper();


    @Override
    public void reLoadTableRelationA(List<LDCode> ldCodes) throws Exception {
        tableRelationMapper.deleteTableRelation();
        ldCodeMapper.deletePRPTableLDCode();
        ldCodeMapper.insertLDCode(ldCodes);
        List<TableRelationShipEntity> tableRelationShipEntityList=tableRelationMapper.getTableReationFromLDcode();
        tableRelationMapper.insertTableRelation(tableRelationShipEntityList);
    }

    @Override
    public void reLoadTableRelation(List<TableRelationShipEntity> tableRelationShipEntities) throws Exception {
        tableRelationMapper.insertTableRelation(tableRelationShipEntities);
    }
}
