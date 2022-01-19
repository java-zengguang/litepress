package com.zg.webdemo.service.ldcode;

import com.sun.org.apache.bcel.internal.generic.LDC;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.LDCode;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface LDCodeService {
    void reLoadPRPTable(List<LDCode> ldCodes) throws Exception;
    List<LDCode> getLDcodeAll() throws Exception;
}
