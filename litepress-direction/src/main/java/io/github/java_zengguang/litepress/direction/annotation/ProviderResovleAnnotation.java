package io.github.java_zengguang.litepress.direction.annotation;


import io.github.java_zengguang.litepress.core.annotation.BaseResolveAnnotation;
import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.direction.entity.ProviderConfig;
import io.github.java_zengguang.litepress.direction.entity.ProviderEntity;
import io.github.java_zengguang.litepress.direction.util.IpConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class ProviderResovleAnnotation extends BaseResolveAnnotation {

    private static ProviderResovleAnnotation pra = null;
    private final ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

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
        return provider.providerName();
    }

    @Override
    public Object getResultValue(Class classes) throws UnknownHostException {
        ProviderEntity provider = new ProviderEntity();
        provider.className = classes.getName();
        InetAddress inetAddress = IpConfig.getLocalHostLANAddress();
        provider.host = inetAddress.getHostAddress();
        provider.port = providerConfig.DTPPort;
        // provider.interfaceName=classes.getInterfaces()[0].getName();
        return provider;
    }

    public Map<String, Object> getProviders() throws ClassNotFoundException, IllegalAccessException, InstantiationException, UnknownHostException {
        return getAnnotationClass( Provider.class);
    }


}
