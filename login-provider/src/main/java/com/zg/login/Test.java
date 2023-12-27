package com.zg.login;

import com.zg.direction.adapter.ProviderAdapter;

public class Test {

    public static void main(String[] args) throws Exception {
        ProviderAdapter providerAdapter = ProviderAdapter.getInstance();
        providerAdapter.init();
    }
}
