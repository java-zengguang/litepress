package com.zg.direction;

import com.zg.direction.register.ZookeeperUtil;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Map;

public class Test {

        public static void main(String args[]) throws IOException, InterruptedException, KeeperException {
            ZookeeperUtil zookeeperUtil= ZookeeperUtil.getInstance("10.200.125.5:2181");
            Map<String,String> list= zookeeperUtil.findChildNodeMap("/SynDataDriverProvider");
            System.out.println(list);

        }

}
