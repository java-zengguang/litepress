package com.zg.admin.dao;

import com.zg.admin.entity.Role;
import com.zg.database.util.BaseDao;
import com.zg.database.util.ModelSQLUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
public class RoleDao extends BaseDao {
    public List<Role> getRoleList(Role role) throws Exception {
        String sql="select *from cn_role ";
        if(role.id!=0){
            sql=sql+" where id="+role.id;
        }
        List list=  select(sql,Role.class);
        return list;
    }

    public int insertRole(Role role) throws SQLException, IllegalAccessException, ClassNotFoundException {
        List list=new ArrayList();
        list.add(role);
         insertTables(list,Role.class,"cn_role");
        return 1;
    }


    public int editRole(Role role) throws NoSuchFieldException, IllegalAccessException, SQLException, ClassNotFoundException {
        String sql="update cn_role set name=#{name} , introduce=#{introduce} where id=#{id} ";
        sql= ModelSQLUtils.dynamicSQL(sql,role);
        return  operation(sql);
    }

    public int deleteRoles(String[] idArray) throws SQLException, ClassNotFoundException {
        List sqlList=new ArrayList();
        for(int i=0;i<idArray.length;i++){
            String id=idArray[i];
            String sql="delete from cn_role where id="+id;
            sqlList.add(sql);
        }
         batchSql(sqlList);
     return 0;
    }
}
