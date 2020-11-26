package com.zg;

import com.zg.direction.adapter.ProviderAdapter;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class Test {

    public static void main(String args[]) throws InterruptedException, IOException, KeeperException, IllegalAccessException {
        ProviderAdapter providerAdapter=new ProviderAdapter();
        providerAdapter.init();
    }
}
