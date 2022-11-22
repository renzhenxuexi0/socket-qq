package com.client.config;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class MultiThreadScheduleTask {

    @Autowired
    private UserService userService;

    // 每隔5s执行一次
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    void updateUserList() {
        Result result3 = new Result();
        result3.setCode(Code.GET_ALL_USERS);
        try {
            Result result4 = userService.getAllUser(result3);

            List<User> allUser = JSON.parseArray(result4.getObject().toString(), User.class);
            List<User> allUser2 = new ArrayList<>();
            allUser.forEach(user -> {
                if (!user.getId().equals(UserMemory.myUser.getId())) {
                    allUser2.add(user);
                }
                if (UserMemory.talkUser != null) {
                    if (UserMemory.talkUser.getId().equals(user.getId())) {
                        UserMemory.talkUser = user;
                    }
                }
            });

            UserMemory.users = allUser2;
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
