package com.zg.login;

import com.zg.direction.adapter.ProviderAdapter;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;

public class Test {

    public static void main(String args[]) throws InterruptedException, IOException, KeeperException, IllegalAccessException, QuorumPeerConfig.ConfigException {
        ProviderAdapter providerAdapter = ProviderAdapter.getInstance();
        providerAdapter.init();
    }
}
