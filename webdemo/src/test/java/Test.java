import com.zg.database.util.JDBCUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/12/16 0016.
 */
public class Test
{
    public static void main(String args[]) throws Exception {
        String sql="select table_name as tableName ,table_type as tableType from information_schema.tables where 1=1 and table_schema='test' ";
        List list=JDBCUtils.select(sql,Test.class);
        System.out.println(list);
    }
}
