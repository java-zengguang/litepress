package io.github.java_zengguang.litepress.web.netty.adapter;


import io.github.java_zengguang.litepress.core.init.Config;
import io.github.java_zengguang.litepress.web.entity.HttpRequestEntity;
import io.github.java_zengguang.litepress.web.entity.HttpResponseEntity;
import io.github.java_zengguang.litepress.web.entity.MVCOption;
import org.tinylog.Logger;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

/**
 * Netty 控制器适配器，使用 Project Reactor 实现响应式异步处理。
 * 
 * Reactor 的优势：
 * 1. 更高效的背压处理 - 内置背压机制，防止消费者被压垮
 * 2. 更好的非阻塞 I/O 支持 - 专为异步非阻塞设计
 * 3. 丰富的操作符 - 提供更多操作符处理复杂异步流程
 * 4. 与 Netty 更好的集成 - Reactor 可以直接使用 Netty 的事件循环
 * 5. 更灵活的调度器 - 提供多种调度器配置
 * 
 * 容器环境优化：
 * - 默认线程数为 CPU 核心数 × 2，避免资源过度占用
 * - 支持通过 MVCOption 自定义配置
 */
public class HttpNettyControllerAdapter extends BaseControllerAdapter {

    private static HttpNettyControllerAdapter nettyControllerAdapter;

    /**
     * 业务处理调度器
     * 使用 BoundedElasticScheduler，适合阻塞 I/O 操作：
     * - 动态创建线程，按需弹性伸缩
     * - 有界线程数，防止资源耗尽
     * - 空闲线程自动回收
     */
    private final Scheduler businessScheduler;

    /**
     * 请求超时时间
     */
    private final Duration requestTimeout;

    /**
     * MVC 配置选项
     */
    private final MVCOption mvcOption;

    private HttpNettyControllerAdapter() {
        // 加载配置
        this.mvcOption = (MVCOption) Config.getConfig("MVCOption");
        
        // 获取配置值
        int maxThreadCount = getActualMaxThreadPoolSize();
        int timeoutSeconds = getActualRequestTimeoutSeconds();
        int idleTimeoutSeconds = getActualThreadIdleTimeoutSeconds();
        boolean enableScheduler = isReactorSchedulerEnabled();

        // 设置请求超时
        this.requestTimeout = Duration.ofSeconds(timeoutSeconds);

        // 初始化调度器
        if (enableScheduler) {
            this.businessScheduler = Schedulers.newBoundedElastic(
                    maxThreadCount,                    // 线程数上限
                    100000,                            // 任务队列上限
                    "http-business-reactor",           // 线程名前缀
                    idleTimeoutSeconds,                // 空闲线程存活时间（秒）
                    true                               // 自动回收空闲线程
            );
            Logger.info("HttpNettyControllerAdapter Reactor 调度器初始化完成 - " +
                    "线程数上限: {}, 超时时间: {}秒, 空闲回收时间: {}秒", 
                    maxThreadCount, timeoutSeconds, idleTimeoutSeconds);
        } else {
            // 不启用调度器，使用立即调度器（在调用线程执行）
            this.businessScheduler = Schedulers.immediate();
            Logger.warn("HttpNettyControllerAdapter Reactor 调度器已禁用，将使用同步方式处理请求（不推荐）");
        }
    }

    /**
     * 获取实际的最大线程数
     */
    private int getActualMaxThreadPoolSize() {
        if (mvcOption != null) {
            return mvcOption.getActualMaxThreadPoolSize();
        }
        // 默认值：CPU 核心数 × 2，适合容器环境
        return Runtime.getRuntime().availableProcessors() * 2;
    }

    /**
     * 获取实际的请求超时时间（秒）
     */
    private int getActualRequestTimeoutSeconds() {
        if (mvcOption != null) {
            return mvcOption.getActualRequestTimeoutSeconds();
        }
        // 默认值：600 秒（10 分钟）
        return 600;
    }

    /**
     * 获取实际的空闲线程存活时间（秒）
     */
    private int getActualThreadIdleTimeoutSeconds() {
        if (mvcOption != null) {
            return mvcOption.getActualThreadIdleTimeoutSeconds();
        }
        // 默认值：60 秒
        return 60;
    }

    /**
     * 是否启用 Reactor 调度器
     */
    private boolean isReactorSchedulerEnabled() {
        if (mvcOption != null) {
            return mvcOption.enableReactorScheduler;
        }
        return true;
    }

    public synchronized static HttpNettyControllerAdapter getInstance() {
        if (nettyControllerAdapter == null) {
            nettyControllerAdapter = new HttpNettyControllerAdapter();
        }
        return nettyControllerAdapter;
    }

    /**
     * 使用 Reactor 响应式处理 HTTP 请求。
     * 返回 Mono<HttpResponseEntity>，支持背压和异步非阻塞。
     *
     * @param requestEntity HTTP 请求实体
     * @return Mono<HttpResponseEntity> 响应式响应结果
     */
    public Mono<HttpResponseEntity> dealHttpRequestReactive(HttpRequestEntity requestEntity) {
        return Mono.fromSupplier(() -> {
                    try {
                        return super.dealHttpRequest(requestEntity);
                    } catch (Exception e) {
                        Logger.error(e, "业务处理异常，请求路径: {}", requestEntity.path);
                        HttpResponseEntity errorResponse = new HttpResponseEntity();
                        errorResponse.statusCode = 500;
                        errorResponse.result = "服务内部错误";
                        return errorResponse;
                    }
                })
                // 使用 BoundedElastic 调度器处理阻塞操作
                .subscribeOn(businessScheduler)
                // 设置超时
                .timeout(requestTimeout)
                // 超时或异常处理
                .onErrorResume(throwable -> {
                    Logger.error("业务处理异常，请求路径: {}, 异常: {}", requestEntity.path, throwable.getMessage());
                    HttpResponseEntity errorResponse = new HttpResponseEntity();
                    if (throwable instanceof TimeoutException) {
                        errorResponse.statusCode = 408;
                        errorResponse.result = "请求处理超时";
                    } else {
                        errorResponse.statusCode = 500;
                        errorResponse.result = "服务内部错误";
                    }
                    return Mono.just(errorResponse);
                });
    }

    /**
     * 异步处理 HTTP 请求，返回 CompletableFuture，不会阻塞调用线程。
     * 内部使用 Reactor 实现，保持接口兼容性。
     *
     * @param requestEntity HTTP 请求实体
     * @return CompletableFuture<HttpResponseEntity> 异步响应结果
     */
    public java.util.concurrent.CompletableFuture<HttpResponseEntity> dealHttpRequestAsync(HttpRequestEntity requestEntity) {
        return dealHttpRequestReactive(requestEntity).toFuture();
    }

    /**
     * 同步处理 HTTP 请求（保持接口兼容性）。
     * 注意：此方法会阻塞当前线程，不推荐在 Netty I/O 线程中直接调用。
     * 建议使用 {@link #dealHttpRequestReactive(HttpRequestEntity)} 或 {@link #dealHttpRequestAsync(HttpRequestEntity)} 异步方法。
     *
     * @param requestEntity HTTP 请求实体
     * @return HTTP 响应实体
     * @throws Exception 处理异常
     */
    @Override
    public HttpResponseEntity dealHttpRequest(HttpRequestEntity requestEntity) throws Exception {
        return super.dealHttpRequest(requestEntity);
    }

    /**
     * 获取业务调度器，供外部使用
     *
     * @return Scheduler 业务调度器
     */
    public Scheduler getBusinessScheduler() {
        return businessScheduler;
    }

    /**
     * 关闭调度器，释放资源
     */
    public void shutdown() {
        Logger.info("HttpNettyControllerAdapter 正在关闭 Reactor 调度器...");
        businessScheduler.dispose();
        Logger.info("HttpNettyControllerAdapter Reactor 调度器已关闭");
    }
}