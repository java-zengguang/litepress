package com.zg.io.service;


import com.zg.common.handler.CommitClassHandler;
import com.zg.io.dao.FileMapper;
import com.zg.io.entity.FileEntity;

import java.util.List;


/**
 * Created by zengguang on 2018/8/31.
 */
public class FileServiceImpl extends CommitClassHandler implements FileService {


    public FileMapper tableMapper = new FileMapper();


    @Override
    public boolean insertFileData(FileEntity fileEntity) {
        return false;
    }

    @Override
    public List<FileEntity> searchFile(FileEntity table) {
        return null;
    }

    @Override
    public FileEntity getFile(String fileId) {
        return null;
    }
}
