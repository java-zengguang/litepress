package com.zg.litepress.chain.exception;

/**
 * 自定义异常基类
 */
public class BaseCustomException extends RuntimeException {

    private static final long serialVersionUID = -778887391066124051L;

    /**
     * 异常信息
     */
    protected String message;

    /**
     * 异常码
     */
    protected int code;

    public BaseCustomException(Throwable cause) {
        super(cause);
    }

    public BaseCustomException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public BaseCustomException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseCustomException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public BaseCustomException(int code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
        this.message = String.format(format, args);
    }

    public BaseCustomException(int code, String format, Throwable cause, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
        this.message = String.format(format, args);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}
