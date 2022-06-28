package com.zhoushl.usercenter.exception;

import com.zhoushl.usercenter.commonn.ErrorCode;

/**
 * 自定义异常
 *
 * @author zhoushl
 */
public class BusinessException extends RuntimeException{

    private final String description;
    private final int code;

    public BusinessException(String message, int code,String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.description = errorCode.getDescription();
        this.code = errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.description = description;
        this.code = errorCode.getCode();
    }

    public String getDescription() {
        return description;
    }


    public int getCode() {
        return code;
    }
}
