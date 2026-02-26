package io.github.java_zengguang.litepress.boot;


import io.github.java_zengguang.litepress.boot.annotation.ScanPackages;
import io.github.java_zengguang.litepress.boot.init.Init;
import io.github.java_zengguang.litepress.boot.init.PackageScan;
import io.github.java_zengguang.litepress.web.netty.reactor.ReactorWebService;
import org.tinylog.Logger;

public abstract class BaseWebAppBoot {
    static {
        Init.init();
    }

    private ReactorWebService tomcatBoot;

    public BaseWebAppBoot(Class<? extends BaseWebAppBoot> clazz, Integer port) {

        tomcatBoot = ReactorWebService.getInstance(port);
        this.initAnnotation(clazz);
        this.config();

    }


    public void init() {
        tomcatBoot.doMain();
    }

    private void initAnnotation(Class clazz) {

        ScanPackages scanPackages = (ScanPackages) clazz.getAnnotation(ScanPackages.class);
        String packages = scanPackages.value();
        Logger.info("扫描路径" + packages);
        PackageScan.scanByAnnotations(packages.split(","));
    }

    public abstract void config();
}
