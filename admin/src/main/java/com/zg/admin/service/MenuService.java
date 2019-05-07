package com.zg.admin.service;

import com.zg.admin.dao.MenuDao;
import com.zg.admin.entity.MenuInfo;
import com.zg.handler.CommitClassHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuService extends CommitClassHandler {

    private MenuDao menuDao=new MenuDao();

    public List getMenuTree(int id,int level,Class modelClass) {
        List list=new ArrayList();
        try {
             list= menuDao.getMenuTree("id",id,level,modelClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }


    public List getMenuById(String id,Class modelClass){
        List list=null;

        if(id!=null) {
            try {
                list = menuDao.getMenuById(Integer.valueOf(id), modelClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public boolean insertMenu(MenuInfo menuInfo){

        try {
            menuInfo.status="0";
            if(menuDao.insertMenu(menuInfo)>0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;

    }


    public boolean updateMenu(MenuInfo menuInfo,Integer id){
        try {
            if(menuDao.updateMenu(menuInfo,id)>0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}
