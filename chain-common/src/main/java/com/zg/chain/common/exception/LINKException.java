package com.zg.chain.common.exception;

import com.zg.chain.common.components.BaseComponent;

public class LINKException extends BaseCustomException {
    private String message;
    private Class<? extends BaseComponent> comparableClass;

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
