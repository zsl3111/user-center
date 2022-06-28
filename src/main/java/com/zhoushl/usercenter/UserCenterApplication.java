package com.zhoushl.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动命令，带参数启动生产环境：java -jar user-center-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
 */
@SpringBootApplication
@MapperScan("com.zhoushl.usercenter.mapper")
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class, args);
    }

}
