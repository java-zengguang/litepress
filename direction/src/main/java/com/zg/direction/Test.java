package com.zg.direction;

import com.zg.direction.register.ZookeeperUtil;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Map;

public class Test {

        public static void main(String args[]) throws IOException, InterruptedException, KeeperException {
            ZookeeperUtil zookeeperUtil= ZookeeperUtil.getInstance("10.7.128.188:2181");
            Map<String,String> map= zookeeperUtil.findChildNodeMap("/SynDataDriverProvider");

            map.forEach((key,value)->{
                System.out.println(key+"-----"+value);
            });
        }

}
