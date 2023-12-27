package com.zg.direction.adapter;


import com.zg.common.util.CommonUtil;
import org.tinylog.Logger;

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

    public static void main(String[] args) throws Exception {
        String rootPath = CommonUtil.getThisPath(ProviderAdapter.class);
        System.setProperty("projectRootPath", rootPath);
        Logger.info(System.getProperty("111" + "projectRootPath"));
        ProviderAdapter providerAdapter = new ProviderAdapter();
        providerAdapter.init();
    }

    public synchronized void init() throws Exception {
        //开始运行
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        providerRegister.doMain();


    }
}
