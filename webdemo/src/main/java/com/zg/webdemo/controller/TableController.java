package com.zg.webdemo.controller;


import com.zg.common.bean.factory.BeanFactory;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.webdemo.entity.PageEntity;
import com.zg.webdemo.entity.Table;
import com.zg.webdemo.service.table.TableService;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;


/**
 * Created by zkyd01 on 2018/8/31.
 */

@Controller("/table")
public class TableController extends BaseController {

    public Map json = new HashedMap();
    public TableService tableService = (TableService) BeanFactory.createBean("tableService");

    @ResultMapping("/searchTableName.do")
    public String searchTableName(Table table) {
        table.setDateBaseName("test");
        json.clear();
        json.put("list", tableService.searchTableName(table));
        json.put("success", true);
        json.put("message", "成功");
        try {
            return "json::" + JsonUtils.objectToJson(json);
        } catch (IllegalAccessException e) {
            Logger.error(e);
            return "string::失败";
        }
    }


    @ResultMapping("/getTableData.do")
    public String getTableData(Table table) {
        json.clear();
        json.put("list", tableService.getTableDate(table));
        try {
            return "json::" + JsonUtils.objectToJson(json);
        } catch (IllegalAccessException e) {
            Logger.error(e);
            return "string::失败";
        }
    }


    @ResultMapping("/getTableDataPage.do")
    public String getTableDataPage(Table table, PageEntity page) {
        json.clear();
        json.put("success", true);
        json.put("primaryKey", "id");
        json.put("list", tableService.getTableDataPage(table, page));
        json.put("page", page);
        try {
            return "json::" + JsonUtils.objectToJson(json);
        } catch (IllegalAccessException e) {
            Logger.error(e);
            return "string::失败";
        }
    }

    @ResultMapping("/toTableList.do")
    public String toTableList() throws Exception {
        Logger.info("转发");
        return "staticURL::/views/tableData.html";
    }


    @ResultMapping("/deleteTableDate.do")
    public String deleteTableDate(Map map) throws IllegalAccessException {
        Logger.info("===tableName===" + map.get("tableName"));
        Logger.info("===id===" + map.get("id"));
        json.clear();
        if (tableService.deleteTableDate(map) > 0) {
            json.put("success", true);
            json.put("message", "删除成功");
        } else {
            json.put("success", false);
            json.put("message", "删除失败");
        }


        return "json::" + JsonUtils.objectToJson(json);

    }
}
