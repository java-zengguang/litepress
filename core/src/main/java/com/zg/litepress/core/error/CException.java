package com.zg.litepress.core.error;

public class CException extends RuntimeException {
    public CException() {
    }

    public CException(String message) {
        super(message);
    }

    public CException(String message, Throwable cause) {
        super(message, cause);
    }

    public CException(Throwable cause) {
        super(cause);
    }

    public CException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
