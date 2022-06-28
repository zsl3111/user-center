package com.zhoushl.usercenter.service;
import java.util.Date;

import com.zhoushl.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author zhoushl
 */
@SpringBootTest
public class UserServiceTest {

    @Autowired
    public UserService userService;

    @Test
    public void addTest(){
        User user = new User();
        user.setUserName("zhoushl");
        user.setUserAccount("123");
        user.setAvatarUrl("");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("345");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);


        boolean result = userService.save(user);

        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "zhoushl";
        String userPassword = "";
        String checkPassword = "123456";
        String invitationCode = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result); // 断言

        userAccount = "zh";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result);

        userAccount = "zhoushl";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result);

        userAccount = "zhou sh";
        userPassword = "12334567";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result);

        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result);

        userAccount = "123";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertEquals(-1,result);

        userAccount = "zhoushl";
        result = userService.userRegister(userAccount, userPassword, checkPassword,invitationCode);
        Assertions.assertTrue(result == -1);

    }
}