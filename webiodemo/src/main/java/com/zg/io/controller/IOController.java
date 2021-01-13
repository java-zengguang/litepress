package com.zg.io.controller;

import com.zg.handler.ProxyUtils;
import com.zg.io.entity.FileEntity;
import com.zg.io.service.FileService;
import com.zg.io.service.FileServiceImpl;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.util.reflect.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/5 0005.
 */
@Controller("/IOController")
public class IOController {


    public FileService fileService = (FileService) ProxyUtils.getProxyClass(new FileServiceImpl(), "insertFileData");


    @ResultMapping("/toUpFile.do")
    public String toUpFile() throws Exception {

         return "privateURL::/WEB-INF/upFile.html";

    }


    @ResultMapping("/upFile.file")
    public  String upFile(String filePath,String logicPath) throws IOException, InterruptedException, IllegalAccessException {

        FileEntity fileEntity=new FileEntity();
        fileEntity.setFileName("");
        fileService.insertFileData(fileEntity);
        Map json=new HashMap<>();

        json.put("path",filePath);
        return "json::"+ JsonUtils.objectToJson(json);
    }

    @ResultMapping("/toDownFile.do")
    public String toDownFile(){
        return "privateURL::/WEB-INF/downFile.html";
    }

    @ResultMapping("/downFile.do")
    public File downFile(String filePath){
        File file=new File(filePath);
        return file;
    }

}
