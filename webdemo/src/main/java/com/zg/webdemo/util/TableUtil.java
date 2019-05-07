package com.zg.webdemo.util;



import com.zg.database.util.JDBCUtils;
import com.zg.webdemo.entity.PageEntity;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by zkyd01 on 2018/9/3.
 */
public class TableUtil {




    private static List<Object> getDateFromSql(String sql) throws Exception{
        System.out.println(TableUtil.class+"====sql==="+sql);
        List list=new ArrayList();
        list=JDBCUtils.selectToMapList(sql);

        return list;
    }


    public static String addPageFromSql(String sql,PageEntity page)throws Exception{
        String countSql=null;
        if(sql!=null){
            countSql="select count(1) as totalResultSize  from ( "+sql+" ) as num";
        }
        List list=getDateFromSql(countSql);
        Map map=(Map)list.get(0);
        Integer totalResultSize=Integer.valueOf( (String)map.get("totalResultSize"));
        page.setTotalResultSize(totalResultSize);
        page.setTotalPageSize(totalResultSize/page.getPageSize());
       Integer startRows=(page.getCurrentPage()-1)*page.getPageSize();
      /*  Integer endRows=(page.getCurrentPage())*page.getPageSize();*/
        sql =sql+ " limit "+startRows+" , "+page.getPageSize();
        return sql;
    }

    public static Integer operationSql(String sql) throws Exception{
        System.out.println(sql);
        JDBCUtils.execute(sql);
        return 1;
    }

    public static Integer insertDate(String tableName,Map<String,String> para) throws Exception{
        String sql="insert into " +tableName;
        String column="";
        String values="";
        Set<String > columnSet=para.keySet();
        for(String c:columnSet){
            column=c+" ,"+column;
            values=para.get(c)+" ,"+"'"+values+"'";
        }
        if(column.endsWith(",")){
            column=column.substring(0,column.length()-1);
        }
        if(values.endsWith(",")){
            values=values.substring(0,values.length()-1);
        }
        sql=sql+" ("+column+")"+" values ("+values+")";
        return operationSql(sql);

    }

/*    public static Integer insertDataLog(String tableName,String createBy,String id)throws Exception{
        Map para=new HashMap();
        para.put("tableName",tableName);
        para.put("id",id);
        List list=getTableData(para);
        String newData=null;
        if(list!=null && list.size()>0){
            Map newDataMap =(Map)list.get(0);
            JSONObject jsonObject = JSONObject.fromObject(newDataMap);
            newData =jsonObject.toString();
        }
        Map paraLog=new HashMap();
        paraLog.put("operation","insert");
        paraLog.put("create_by",createBy);
        paraLog.put("create_date",new Date());
        paraLog.put("old_data","");
        paraLog.put("new_data",newData);
        return insertDate(tableName,paraLog);

    }
    */


  /*  public static List<Object> getTableData(Map<String,String> para) throws Exception{
          List list=new ArrayList();
                String sql=getSql(para);
               list=getDateFromSql(sql);
            return list;
        }*/



/*    public static List<Object> getTableDataPage(Map<String,String> para,PageEntity page) throws Exception{
        List list=new ArrayList();
        String sql=getSql(para);
        sql=addPageFromSql(sql,page);
        list=getDateFromSql(sql);
        return list;
    }*/


    /*   private static String getSql(Map<String,String> para) {
        String sql=null;
        String tableName = para.get("tableName");
        String id=para.get("id");
        String createBy = para.get("createBy");
        String updateBy = para.get("updateBy");
        String creatDate = para.get("createDate");
        String updateDate = para.get("updateDate");
        if (tableName != null) {
            sql= "select * from " + tableName;

                sql = sql + " where ";
                if (id != null && id!="") {
                    sql = sql + " id=" + id + " and";
                }
                if (creatDate != null && creatDate!="") {
                    sql = sql + " creat_date=" + creatDate + " and";
                }
                if (updateDate != null && updateDate!="") {
                    sql = sql + " update_date=" + updateDate + " and";
                }
                if (createBy != null && createBy!="") {
                    sql = sql + " create_by=" + createBy + " and";
                }
                if (updateBy != null && updateBy!="") {
                    sql = sql + " update_by=" + updateBy + " and";
                }

            if(sql.endsWith("where ")){
                sql=sql.substring(0, sql.length() - 6);
            }
            if(sql.endsWith("and")){
                sql = sql.substring(0, sql.length() - 3);
            }

        }
        return sql;
    }*/

}
