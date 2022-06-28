package com.zhoushl.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zhoushl
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -1797421715839042588L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String invitationCode;
}
