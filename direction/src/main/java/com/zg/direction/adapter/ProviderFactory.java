package com.zg.direction.adapter;


import com.zg.direction.annotation.ProviderResovleAnnotation;
import com.zg.direction.entity.ProviderConfig;
import com.zg.common.init.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProviderFactory {

    private static ProviderFactory providerFactory = null;

    private Logger logger = LoggerFactory.getLogger(ProviderFactory.class);

    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");
    private Map<String, Object> providerMap;


    private ProviderFactory() {
    }

    public static synchronized ProviderFactory getInstance() {
        if (providerFactory == null) {
            providerFactory = new ProviderFactory();
        }

        return providerFactory;

    }

    public void loadProvider() {
        ProviderResovleAnnotation pra = ProviderResovleAnnotation.getInstance();
        try {
            if (providerMap == null) {
                providerMap = pra.getProviders();
            }
        } catch (ClassNotFoundException e) {
            logger.error("ProviderAdapter初始化错误", e);
        } catch (IllegalAccessException e) {
            logger.error("ProviderAdapter初始化错误", e);
        } catch (InstantiationException e) {
            logger.error("ProviderAdapter初始化错误", e);
        }
    }


    public Map getProviderMap() {
        loadProvider();
        return providerMap;
    }
}
