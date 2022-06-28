package com.zhoushl.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 * 一般在接受参数的时候实现序列化
 * @author zhoushl
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -4253205913535655661L;

    private String userAccount;

    private String userPassword;
}
