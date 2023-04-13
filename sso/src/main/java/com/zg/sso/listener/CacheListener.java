package com.zg.sso.listener;

import com.zg.incache.util.RomCacheUtil;
import com.zg.incache.util.SimpleRomCache;
import com.zg.sso.entity.UserLogin;
import org.tinylog.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CacheListener implements ServletContextListener {

    public CacheListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
        try {
            // 需要实现的功能
            Logger.info("随项目启动方式一----------------》");
            RomCacheUtil.coverPut("LoginCache", new SimpleRomCache(UserLogin.class));
        } catch (Exception e) {
            Logger.error("GreyClientInitListener error", e);
        }
    }
}
