package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FXMLController
public class UserInterfaceController implements Initializable {


    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    @Autowired
    private UserService userService;
    // 用户界面的容器
    @FXML
    private GridPane ui;

    /**
     * 每隔一段时间更新用户列表
     */
    private void regularlyUpdateUser() {

        Result result = new Result();
        result.setCode(Code.GET_ALL_USERS);

        /*
            创键一个定时任务，不断执行
         */
        scheduledExecutorService.schedule(() -> {
            Result result2 = userService.getAllUser(result);
            UserMemory.users = JSON.parseArray(result2.getObject().toString(), User.class);
            buildUserList();
        }, 5, TimeUnit.SECONDS);
    }

    void buildUserList() {
        GridPane gridPane = new GridPane();// 创建一个gridPane
        ScrollPane scrollPane = new ScrollPane();// 同上
        ui.add(scrollPane, 0, 1);
        scrollPane.setContent(gridPane);
        // 不断生成新的用户状态栏
        for (int i = 0; i < UserMemory.users.size(); i++) {
            gridPane.add(new Label(UserMemory.users.get(i).getUsername()), 0, i);
            if (UserMemory.users.get(i).getLogin() == 1) {
                gridPane.add(new Circle(10, Color.rgb(30, 144, 255)), 1, i);
            } else {
                gridPane.add(new Circle(10, Color.rgb(172, 172, 172)), 1, i);
            }
            // 设置每一行的高度
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(40);
            gridPane.getRowConstraints().add(i, rowConstraints);
        }
        // 设置每列布局
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setPrefWidth(274);
        gridPane.getColumnConstraints().add(0, columnConstraints);

        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setHalignment(HPos.CENTER);
        columnConstraints2.setPrefWidth(30);
        gridPane.getColumnConstraints().add(1, columnConstraints2);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buildUserList();
        regularlyUpdateUser();
    }

    public void clickMyHeadButton(MouseEvent mouseEvent) {
    }
}
