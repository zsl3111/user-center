package com.zhoushl.usercenter.service;

import com.zhoushl.usercenter.commonn.BaseResponse;
import com.zhoushl.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
* @author zhoushenglei
 * 用户服务
* @description 针对表【user】的数据库操作Service
* @createDate 2022-04-24 22:29:51
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @param invitationCode 邀请码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword,String invitationCode);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @param request
     * @return 脱敏用户
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户查询
     * @param userName 用户名
     * @return 用户集合
     */
    List<User> getUsers(String userName);


    /**
     * 用户脱敏
     * @param originUser 原来的user
     * @return 脱敏后的user
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param session session
     * @return int
     */
    BaseResponse<Integer> userLogOut(HttpSession session);
}
