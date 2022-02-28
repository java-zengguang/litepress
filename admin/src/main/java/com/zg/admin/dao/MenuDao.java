package com.zg.admin.dao;

import com.zg.admin.entity.Menu;
import com.zg.admin.entity.MenuInfo;
import com.zg.database.util.JDBCUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDao {

    public List getMenuByPid(int pid, int level, Class modelClass) throws Exception {

        String sql="select *from cn_menu where 1=1 and pid="+pid+"  and level="+level;
        List list=JDBCUtils.select(sql,modelClass);
        return list;

    }


    public List getMenuById(int id, Class modelClass) throws Exception {
        String sql="select *from cn_menu where 1=1 and id="+id;
        List list=JDBCUtils.select(sql,modelClass);
        return list;

    }


    public List getMenuById(int id,int level, Class modelClass) throws Exception {
        String sql="select *from cn_menu where 1=1 and id="+id+"  and level="+level;
        List list=JDBCUtils.select(sql,modelClass);
        return list;

    }


    public List getMenuTree(String keyType, int key, int level, Class modelClass) throws Exception {

        List list=new ArrayList<>();
        if("pid".equals(keyType)){
           list =getMenuByPid(key,level,modelClass);
        }

        if("id".equals(keyType)){
            list=getMenuById(key,level,modelClass);
        }

        if(list!=null) {
            for (Object object : list) {
                if (object instanceof Menu) {
                    Menu menu=(Menu)object;
                    int pid = menu.id;
                    List childMenuList = getMenuTree("pid", pid, level + 1, modelClass);
                    if (childMenuList == null) {
                        continue;
                    } else {
                        menu.children = childMenuList;
                    }
                }
                if (object instanceof MenuInfo) {
                    MenuInfo menu=(MenuInfo)object;
                    int pid = menu.id;
                    List childMenuList = getMenuTree("pid", pid, level + 1, modelClass);
                    if (childMenuList == null) {
                        continue;
                    } else {
                        menu.children = childMenuList;
                    }
                }
            }
            }



        return list;

    }


    public int insertMenu(MenuInfo menuInfo) throws SQLException, IllegalAccessException {

       return JDBCUtils.insertTable(menuInfo);

    }


    public int updateMenu(MenuInfo menuInfo,Integer id) throws SQLException, IllegalAccessException {


       return JDBCUtils.updateModel(menuInfo,"id="+id);
    }



    public static void main(String args[]) throws Exception {
        MenuDao menuDao=new MenuDao();

        List list=menuDao.getMenuTree("id",1,0,MenuInfo.class);
        System.out.println(list);
    }





}
