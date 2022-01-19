package com.zg.sinosing.load;

import com.zg.webdemo.entity.DatabaseTableStructureEntity;

import java.util.List;

public interface LoadDataBaseStructure {

    boolean loadStructure() throws Exception;
    boolean reLoadStructure() throws Exception;

}
