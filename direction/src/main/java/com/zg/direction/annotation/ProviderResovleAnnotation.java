package com.zg.direction.annotation;

import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.init.Config;
import com.zg.util.annotation.BaseResolveAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProviderResovleAnnotation extends BaseResolveAnnotation {

    private static ProviderResovleAnnotation pra = null;
    private Logger logger = LoggerFactory.getLogger(ProviderResovleAnnotation.class);
    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private ProviderResovleAnnotation() {
    }

    public static ProviderResovleAnnotation getInstance() {
        synchronized (ProviderResovleAnnotation.class) {
            if (pra == null) {
                pra = new ProviderResovleAnnotation();
            }
        }
        return pra;
    }

    @Override
    public String getResultName(Object annotationObject) throws IllegalAccessException, InstantiationException {
        Provider provider = (Provider) annotationObject;
        String providerName = provider.providerName();
        return providerName;
    }

    @Override
    public Object getResultValue(Class classes) {
        ProviderEntity provider = new ProviderEntity();
        provider.className = classes.getName();
        provider.host = providerConfig.DTPHost;
        provider.port = providerConfig.DTPPort;
        // provider.interfaceName=classes.getInterfaces()[0].getName();
        return provider;
    }

    public Map<String, Object> getProviders() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Map<String, Object> providerMap = getAnnotationClass(providerConfig.packages, Provider.class);
        return providerMap;
    }


}
