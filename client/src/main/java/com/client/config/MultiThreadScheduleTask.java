package com.client.config;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MultiThreadScheduleTask {

    @Autowired
    private UserService userService;

    // 每隔5s执行一次
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    void updateUserList() {
        Result result3 = new Result();
        result3.setCode(Code.GET_ALL_USERS);
        Result result4 = userService.getAllUser(result3);
        UserMemory.users = JSON.parseArray(result4.getObject().toString(), User.class);
    }
}
