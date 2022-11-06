package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserInterfaceController {

    // 用户界面的容器
    @FXML
    private GridPane ui;

    private final UserService userService = new UserService();

    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    /**
     * 每隔一段时间更新用户列表
     */
    private void regularlyUpdateUser() {

        Data data = new Data();
        data.setCode(Code.GET_USERS);

        /*
            创键一个定时任务，不断执行
         */
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                Data data2 = userService.getAllUser(data);
                if (Code.GET_SUCCESS.equals(data2.getCode())) {
                    UserMemory.users = JSON.parseArray(data2.getObject().toString(), User.class);
                }
            }
        }, 60, TimeUnit.SECONDS);
    }

}
