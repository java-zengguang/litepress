package com.zg.sinosig.load;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface LoadDataBaseStructure {

    boolean reLoadStructure(List<SinoSigSQLLogEntity> list) throws Exception;
    boolean loadStructure() throws Exception;


}
