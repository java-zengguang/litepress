package com.zg.error;

import org.python.antlr.ast.Str;

public  class CError {

    private String errorCode;
    private String errorMsg;
    private String errorType;

    public String getErrorType(){
        return errorType;
    }

    public  String getErrorCode(){
        return errorCode;
    }
    public  String getErrorMsg(){
        return errorMsg;
    }
}
