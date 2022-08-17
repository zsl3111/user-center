package com.zhoushl.usercenter.controller;

import com.zhoushl.usercenter.commonn.BaseResponse;
import com.zhoushl.usercenter.commonn.ErrorCode;
import com.zhoushl.usercenter.commonn.ResultUtils;
import com.zhoushl.usercenter.exception.BusinessException;
import com.zhoushl.usercenter.model.domain.User;
import com.zhoushl.usercenter.model.domain.request.UserLoginRequest;
import com.zhoushl.usercenter.model.domain.request.UserRegisterRequest;
import com.zhoushl.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static com.zhoushl.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.zhoushl.usercenter.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * 用户接口
 *
 * @author zhoushl
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    /**
     * @requestBody的作用：前端传来多个参数的时候，不需要一个一个参数去接受，用一个注解加一个实体就能全部接受
     * @param userRegisterRequest 请求体
     * @return 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

        if(userRegisterRequest == null){
            //return null;
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String invitationCode = userRegisterRequest.getInvitationCode();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword, invitationCode);
        return ResultUtils.success(id);
    }

    @PostMapping("login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @GetMapping("search")
    public BaseResponse<List<User>> getUsers(String userName, HttpServletRequest request){

        if(!isAdmin(request)){
            //return ResultUtils.success(new ArrayList<>());
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        List<User> users = userService.getUsers(userName);
        return ResultUtils.success(users);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> userDelete(@RequestParam Long id, HttpServletRequest request){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        if(id <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean isOk = userService.removeById(id);
        return ResultUtils.success(isOk);
    }

    /**
     * 是否为管理员
     * @param request request
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request){
        // 鉴权
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User) userObj;

        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 获取当前用户
     * @param session session
     * @return user
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpSession session){
        Object userObj = session.getAttribute(USER_LOGIN_STATUS);
        User currentUser = (User) userObj;
        if(currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        long id = currentUser.getId();
        // 这里要查询一次数据库的原因是考虑到因session更新不及时，用户的信息更新不及时
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 注销
     * @param session session
     * @return integer
     */
    @PostMapping("loginOut")
    public BaseResponse<Integer> userLoginOut(HttpSession session){
        if (session == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        return userService.userLogOut(session);
    }
}
