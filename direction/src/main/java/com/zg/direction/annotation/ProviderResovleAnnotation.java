package com.zg.direction.annotation;

import com.zg.common.annotation.BaseResolveAnnotation;
import com.zg.common.init.Config;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.util.IpConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ProviderResovleAnnotation extends BaseResolveAnnotation {

    private static ProviderResovleAnnotation pra = null;
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
    public Object getResultValue(Class classes) throws UnknownHostException {
        ProviderEntity provider = new ProviderEntity();
        provider.className = classes.getName();
        InetAddress inetAddress = IpConfig.getLocalHostLANAddress();
        String ipAddresss = inetAddress.getHostAddress();
        provider.host = ipAddresss;
        provider.port = providerConfig.DTPPort;
        // provider.interfaceName=classes.getInterfaces()[0].getName();
        return provider;
    }

    public Map<String, Object> getProviders() throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnknownHostException {

        Map<String, Object> providerMap = getAnnotationClass(providerConfig.packages, Provider.class);
        return providerMap;
    }


}
