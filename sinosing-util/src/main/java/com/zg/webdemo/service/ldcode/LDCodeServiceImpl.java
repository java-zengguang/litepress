package com.zg.webdemo.service.ldcode;

import com.zg.handler.CommitClassHandler;
import com.zg.webdemo.dao.DatabaseTableStructureMapper;
import com.zg.webdemo.dao.LDCodeMapper;
import com.zg.webdemo.entity.DatabaseTableStructureEntity;
import com.zg.webdemo.entity.LDCode;

import javax.naming.ldap.LdapContext;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LDCodeServiceImpl extends CommitClassHandler implements LDCodeService {

    LDCodeMapper mapper=new LDCodeMapper();

    @Override
    public void reLoadPRPTable(List<LDCode> ldCodes) throws Exception{
        mapper.deletePRPTableLDCode();
        mapper.insertLDCode(ldCodes);
    }

    @Override
    public List<LDCode> getLDcodeAll() throws Exception {
        return mapper.getLDcodeAll();
    }
}
