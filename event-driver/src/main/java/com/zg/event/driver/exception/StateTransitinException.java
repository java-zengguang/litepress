package com.zg.event.driver.exception;

public class StateTransitinException extends Exception {
    public StateTransitinException() {
    }

    public StateTransitinException(String message) {
        super(message);
    }

    public StateTransitinException(String message, Throwable cause) {
        super(message, cause);
    }

    public StateTransitinException(Throwable cause) {
        super(cause);
    }

    public StateTransitinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
