package io.github.java_zengguang.litepress.db.dao.pool;

import io.github.java_zengguang.litepress.core.bean.entity.OptionDB;
import org.tinylog.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class ZGDBPDataSource {

    private static ZGDBPDataSource zgdbp = null;
    private static int MAX_CONN_SIZE = 10;
    private static Integer MAX_POOL_SIZE = 5;
    //  public final ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
    private static final Connection[] conn = new Connection[MAX_POOL_SIZE];
    private static final List connectPool = new ArrayList();
    private static int flag = 0;
    private String url = null;
    private String Driver = null;
    private String username = null;
    private String password = null;


    //构造方法
    private ZGDBPDataSource(Map<String, String> option) {
        setConfig(option);
    }


    //构造方法
    private ZGDBPDataSource(OptionDB optionDB) {
        this.url = optionDB.getUrl();
        MAX_CONN_SIZE = optionDB.getMaxPoolSize();
        Driver = optionDB.getDriver();
        this.username = optionDB.getUsername();
        this.password = optionDB.getPassword();
    }

    public static ZGDBPDataSource getInstance(OptionDB optionDB) {
        if (zgdbp == null) {
            zgdbp = new ZGDBPDataSource(optionDB);
        }
        return zgdbp;
    }

    public static ZGDBPDataSource getInstance(Map map) {
        if (zgdbp == null) {
            zgdbp = new ZGDBPDataSource(map);
        }
        return zgdbp;
    }


    private void setConfig(Map<String, String> option) {

        this.url = option.get("jdbcUrl");
        MAX_CONN_SIZE = Integer.parseInt(option.get("maxPoolSize"));  //最大连接数
        this.Driver = option.get("driverClass");
        this.username = option.get("user");
        this.password = option.get("password");
        MAX_POOL_SIZE = Integer.valueOf(option.get("acquireIncrement"));  //一次性获取的connection数
    }


    //初始化线程池
    private void inits() {
        try {
            for (int i = 0; i < MAX_CONN_SIZE; i++) {
                conn[i % MAX_POOL_SIZE] = getConnectFromDatabase();
                connectPool.add(i, conn[i % MAX_POOL_SIZE]);
            }

            flag = 1;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //创建一个数据库连接
    private Connection getConnectFromDatabase() {
        Connection conn = null;

        try {

            Class.forName(Driver);
            conn = DriverManager.getConnection(url, username, password);
            conn = (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(), conn.getClass().getInterfaces(), new ZGDBPConnection(conn));

        } catch (Exception e) {

            Logger.error(e);
        }
        return conn;
    }


    //获取一个线程池里的数据
    public Connection getConnection() {
        if (flag == 0) {
            inits();
        }
        Connection conn = null;
        if (connectPool.size() == 0) {
            try {
                Thread.sleep(1000);
                getConnection();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                Logger.error(e);
            }
        } else {
            conn = (Connection) connectPool.remove(0);
        }

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            Logger.error(e);
        }

        //   tl.set(conn);
        return conn;
    }

    //重置连接
    public boolean release(Connection conn) {
        return connectPool.add(conn);
    }


    public class ZGDBPConnection implements InvocationHandler {
        private final Connection target;

        public ZGDBPConnection(Connection target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            Object result = null;
            if ("close".equals(method.getName())) {
                release(target);
            } else {
                result = method.invoke(target, args);
            }

            return result;

        }
    }


}
