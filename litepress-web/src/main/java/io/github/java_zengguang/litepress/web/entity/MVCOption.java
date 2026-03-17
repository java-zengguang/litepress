package io.github.java_zengguang.litepress.web.entity;

import io.github.java_zengguang.litepress.core.bean.entity.MainModel;

/**
 * MVC 配置选项。
 */
public class MVCOption extends MainModel {
    public String controllerPackage;
    public String controllerSuffix;
    public String upLoadSuffix;
    public String upLoadPackage;
    public String upLoadPath;
    public String temporaryFilePath;
    public String projectRoot;

    public String powerLevel = "0";   //0-不拦截  1-拦截未登录的  默认为0

    // ============ Reactor 线程池配置 ============

    /**
     * 业务处理请求超时时间（秒）。
     * 默认 600 秒（10 分钟），适合长时间运行的业务请求。
     */
    public int requestTimeoutSeconds = 600;

    /**
     * Reactor 调度器最大线程数。
     * 默认为 CPU 核心数 × 2，适合容器环境。
     * 如果为 0 或负数，则使用默认值 CPU 核心数 × 2。
     */
    public int maxThreadPoolSize = 0;

    /**
     * Reactor 调度器空闲线程存活时间（秒）。
     * 默认 60 秒，空闲线程会被自动回收。
     */
    public int threadIdleTimeoutSeconds = 60;

    /**
     * 是否启用 Reactor 调度器。
     * 默认为 true。如果为 false，将使用同步方式处理请求（不推荐）。
     */
    public boolean enableReactorScheduler = true;

    /**
     * 获取实际的最大线程数。
     * 如果未配置或配置无效，返回默认值（CPU 核心数 × 2，适合容器环境）。
     *
     * @return 最大线程数
     */
    public int getActualMaxThreadPoolSize() {
        if (maxThreadPoolSize > 0) {
            return maxThreadPoolSize;
        }
        // 容器环境默认使用 CPU 核心数 × 2，避免资源过度占用
        return Runtime.getRuntime().availableProcessors() * 2;
    }

    /**
     * 获取实际的请求超时时间（秒）。
     *
     * @return 超时时间
     */
    public int getActualRequestTimeoutSeconds() {
        return requestTimeoutSeconds > 0 ? requestTimeoutSeconds : 600;
    }

    /**
     * 获取实际的空闲线程存活时间（秒）。
     *
     * @return 空闲线程存活时间
     */
    public int getActualThreadIdleTimeoutSeconds() {
        return threadIdleTimeoutSeconds > 0 ? threadIdleTimeoutSeconds : 60;
    }

    public MVCOption clone() {
        MVCOption config = null;
        config = (MVCOption) super.clone();
        return config;
    }
}
