package com.zg.sso.listener;

import com.zg.cache.util.RomCacheUtil;
import com.zg.cache.util.SimpleRomCache;
import com.zg.sso.entity.UserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CacheListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheListener.class);

    public CacheListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
        try {
            // 需要实现的功能
            System.out.println("随项目启动方式一----------------》");
            RomCacheUtil.coverPut("LoginCache", new SimpleRomCache(UserLogin.class));
        } catch (Exception e) {
            LOGGER.error("GreyClientInitListener error", e);
        }
    }
}
