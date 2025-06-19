package com.zg.direction.register;

import com.zg.common.init.Config;
import com.zg.common.util.CommonUtil;
import com.zg.direction.entity.ZooKeeperConfig;
import com.zg.direction.util.IpConfig;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.tinylog.Logger;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Properties;


public class ZookeeperBoot implements Runnable {


    private static final String USAGE =
            "Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]";
    Object obj;
    private ServerCnxnFactory cnxnFactory;

    public ZookeeperBoot(Object object) {
        this.obj = object;
    }

    public static void main(String[] args) throws Exception {
        String rootPath = CommonUtil.getThisPath(ZookeeperBoot.class);
        System.setProperty("projectRootPath", rootPath);
        ZookeeperBoot zookeeperBoot = new ZookeeperBoot("");
        zookeeperBoot.run();

    }

    @Override
    public void run() {

        try {

            ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
            ZooKeeperConfig zooKeeperConfig = (ZooKeeperConfig) Config.getConfig("zooKeeperConfig");
            Class classes = zooKeeperConfig.getClass();
            Field[] fields = classes.getFields();
            Properties properties = new Properties();
            for (Field field : fields) {
                properties.setProperty(field.getName(), (String) field.get(zooKeeperConfig));
            }
            // properties.setProperty("clientPortAddress", "127.0.0.1");
            InetAddress inetAddress = IpConfig.getLocalHostLANAddress();
            String ipAddresss = inetAddress.getHostAddress();
            properties.setProperty("clientPortAddress", ipAddresss);
            QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
            quorumConfig.parseProperties(properties);
            ServerConfig config = new ServerConfig();
            config.readFrom(quorumConfig);
            zkServer.runFromConfig(config);


        } catch (Exception e) {
            Logger.error(e);
        }

    }


}
