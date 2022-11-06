package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

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
                buildUserList();
            }
        }, 2, TimeUnit.SECONDS);
    }

    void buildUserList() {
        GridPane gridPane = new GridPane();// 创建一个gridPane
        ScrollPane scrollPane = new ScrollPane();// 同上
        ui.add(scrollPane, 0, 1);
        scrollPane.setContent(gridPane);
        // 不断生成新的用户状态栏
        for (int i = 0; i < UserMemory.users.size(); i++) {
            gridPane.add(new Label(), 0, i);
            if (UserMemory.users.get(i).getLogin() == 1) {
                gridPane.add(new Circle(10, Color.rgb(30, 144, 255)), 1, i);
            } else {
                gridPane.add(new Circle(10, Color.rgb(172, 172, 172)), 1, i);
            }

        }
    }


}
