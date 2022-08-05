package com.zg.sso.listener;

import com.zg.incache.util.RomCacheUtil;
import com.zg.incache.util.SimpleRomCache;
import com.zg.sso.entity.UserLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CacheListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(CacheListener.class);

    public CacheListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
        try {
            // 需要实现的功能
            logger.info("随项目启动方式一----------------》");
            RomCacheUtil.coverPut("LoginCache", new SimpleRomCache(UserLogin.class));
        } catch (Exception e) {
            logger.error("GreyClientInitListener error", e);
        }
    }
}
