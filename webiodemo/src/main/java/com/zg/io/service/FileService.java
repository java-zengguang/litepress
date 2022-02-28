package com.zg.io.service;



import com.zg.io.entity.FileEntity;


import java.util.List;

/**
 * Created by zkyd01 on 2018/8/31.
 */


public interface FileService {

    boolean insertFileData(FileEntity fileEntity);

    List<FileEntity> searchFile(FileEntity table);

    FileEntity getFile(String fileId);


}
