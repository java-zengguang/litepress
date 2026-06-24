package io.github.java_zengguang.litepress.direction.adapter;


public class ProviderAdapter {


    private static ProviderAdapter providerAdapter = null;

    private ProviderAdapter() {
    }

    public synchronized static ProviderAdapter getInstance() {
        if (providerAdapter == null) {
            providerAdapter = new ProviderAdapter();
        }
        return providerAdapter;
    }


    public synchronized void init() throws Exception {
        //开始运行
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        providerRegister.doMain();


    }
}
