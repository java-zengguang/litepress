package io.github.java_zengguang.litepress.core.log;

public class TraceIdHolder {
    private static final ThreadLocal<String> traceId = new ThreadLocal<>();

    public static String getTraceId() {
        return traceId.get();
    }

    public static void setTraceId(String value) {
        traceId.set(value);
    }

    public static void clearTraceId() {
        traceId.remove();
    }
}