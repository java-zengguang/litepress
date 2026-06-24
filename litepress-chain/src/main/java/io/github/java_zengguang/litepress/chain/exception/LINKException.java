package io.github.java_zengguang.litepress.chain.exception;

import io.github.java_zengguang.litepress.chain.components.BaseComponent;

public class LINKException extends BaseCustomException {
    private final String message;
    private final Class<? extends BaseComponent> comparableClass;

    public LINKException(Class<? extends BaseComponent> comparableClass, String message) {
        super(2, message);
        this.comparableClass = comparableClass;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LINKException| " + comparableClass.getName() + ":" + this.message;
    }

}
