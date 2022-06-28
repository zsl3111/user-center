package com.zhoushl.usercenter.exception;

import com.zhoushl.usercenter.commonn.BaseResponse;
import com.zhoushl.usercenter.commonn.ErrorCode;
import com.zhoushl.usercenter.commonn.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * spring框架带的切面注解，可以在代码前或代码后做一些额外得处理
 * 全局异常处理，使前端能够接收到更优雅的报错信息，而不是直接返回一串错误代码
 *
 * @author zhoushl
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException businessException){
        log.error("runtimeException:"+businessException);
        return ResultUtils.error(businessException.getCode(),businessException.getMessage(),businessException.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException runtimeException){
        log.error("runtimeException:"+runtimeException);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,runtimeException.getMessage());
    }
}
