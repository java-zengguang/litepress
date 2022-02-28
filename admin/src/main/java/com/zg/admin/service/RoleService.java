package com.zg.admin.service;

import com.zg.admin.dao.RoleDao;
import com.zg.admin.entity.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class RoleService implements RoleServiceInte{

    private RoleDao roleDao=new RoleDao();

    public List getRoleList(Role role) {
        List list=new ArrayList();
        try {
            list= roleDao.getRoleList(role);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertRole(Role role) {

        int result= 0;
        try {
           result= roleDao.insertRole(role);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if(result>0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean editRole(Role role) {

        int result= 0;
        try {
            result=roleDao.editRole(role);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(result>0){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public boolean deleteRoles(String ids) throws SQLException {
        System.out.println("ids"+ids);
        String idArray[]=ids.split(",");
        roleDao.deleteRoles(idArray);
        return true;
    }


}
