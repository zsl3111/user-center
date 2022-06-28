package com.zhoushl.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhoushl.usercenter.commonn.BaseResponse;
import com.zhoushl.usercenter.commonn.ErrorCode;
import com.zhoushl.usercenter.commonn.ResultUtils;
import com.zhoushl.usercenter.exception.BusinessException;
import com.zhoushl.usercenter.service.UserService;
import com.zhoushl.usercenter.model.domain.User;
import com.zhoushl.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zhoushl.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

/**
* @author zhoushenglei
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-04-24 22:29:51
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    // 加盐
    private static final String SALT = "zhoushl";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String invitationCode) {

        // 校验
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4位");
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码小于8位");
        }
        if(!StringUtils.equals(userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        if(StringUtils.isNotBlank(invitationCode) && invitationCode.length() > 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"邀请码长度大于4位");
        }
        // 账户不能包含特殊字符
        String validPattern = "\\\\pP|\\\\pS|\\\\s+";

        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名包含特殊字符");
        }

        // 判断用户名是否已经注册,这一步操作最好放在最后面，因为操作涉及查询数据库，所以等前面都校验通过在查，避免性能浪费
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"该用户名已存在");
        }

        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        // 保存数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(md5Password);
        user.setInvitationCode(invitationCode);
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败");
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        // 校验
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4位");
        }
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码小于8位");
        }

        // 账户不能包含特殊字符
        String validPattern = "\\\\pP|\\\\pS|\\\\s+";

        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if(matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户名包含特殊字符");
        }

        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        // 判断用户名是否已经注册,这一步操作最好放在最后面，因为操作涉及查询数据库，所以等前面都校验通过在查，避免性能浪费
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",md5Password);
        User user = userMapper.selectOne(queryWrapper);

        // 用户不存在
        if(user == null){
            log.info("user login failed,userAccount account cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }

        // 用户脱敏
        User safetyUser = getSafetyUser(user);

        // 记录用户的登陆态
        request.getSession().setAttribute(USER_LOGIN_STATUS,safetyUser);

        return safetyUser;
    }

    @Override
    public List<User> getUsers(String userName) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(userName)){
            queryWrapper.like("userName",userName);
        }
        List<User> users = userMapper.selectList(queryWrapper);

        // 转换为流-> 遍历 -> 设置每一个对象的属性 -> 返回 -> 最后转换为list
        /*return users.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());*/

        // 双冒号前面是类名，后面是方法名
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 用户脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setInvitationCode(originUser.getInvitationCode());
        return safetyUser;
    }

    @Override
    public BaseResponse<Integer> userLogOut(HttpSession session){
        session.removeAttribute(USER_LOGIN_STATUS);
        return ResultUtils.successWithoutData();
    }
}




