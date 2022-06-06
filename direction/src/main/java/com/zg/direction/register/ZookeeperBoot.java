package com.zg.direction.register;

import com.zg.common.init.Config;
import com.zg.direction.entity.ZooKeeperConfig;
import org.apache.zookeeper.server.*;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ZookeeperBoot implements Runnable{
    private static final Logger LOG =
            LoggerFactory.getLogger(ZooKeeperServerMain.class);

    private static final String USAGE =
            "Usage: ZooKeeperServerMain configfile | port datadir [ticktime] [maxcnxns]";

    private ServerCnxnFactory cnxnFactory;

    Object obj;

    public ZookeeperBoot(Object object) {
        this.obj = object;
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
            properties.setProperty("clientPortAddress", "127.0.0.1");
            QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
            quorumConfig.parseProperties(properties);
            ServerConfig config = new ServerConfig();
            config.readFrom(quorumConfig);
            zkServer.runFromConfig(config);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws Exception {

        ZookeeperBoot zookeeperBoot=new ZookeeperBoot("");
        zookeeperBoot.run();

    }


}
