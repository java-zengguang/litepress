package com.zg.litepress.chain.exception;

import com.zg.litepress.chain.components.BaseBatchComponent;

public class BATCHException extends BaseCustomException {

    private final Class<? extends BaseBatchComponent> comparableClass;

    public BATCHException(Class<? extends BaseBatchComponent> comparableClass, String message) {
        super(1, message);
        this.message = message;
        this.comparableClass = comparableClass;
    }

    @Override
    public String toString() {
        return "LINKException| " + comparableClass.getName() + ":" + this.message;
    }
}
