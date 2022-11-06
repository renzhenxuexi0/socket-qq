package com.client.controller;

import com.client.application.RegisterApplication;
import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginInterfaceController {
    private final UserService userService = new UserService();
    @FXML
    private Button loginButton;

    @FXML
    private TextField accountInput;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void loginButtonEvent(ActionEvent event) {
        Data data = new Data();
        data.setCode(Code.USER_LOGIN);
        User user = new User();
        user.setAccount(accountInput.getText());
        user.setPassword(passwordInput.getText());
        data.setObject(user);
        Data data2 = userService.userLogin(data);
        Alert alert;
        if (Code.LOGIN_SUCCESS.equals(data2.getCode())) {
            StageManager.jump("登录界面", "用户界面");
            System.out.println("success！");
        } else {
            // 注册失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, data2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }

    @FXML
    void registerButtonEvent(ActionEvent event) throws Exception {
        RegisterApplication registerApplication = new RegisterApplication();
        registerApplication.start(new Stage());
        StageManager.jump("登录界面", "注册界面");
    }
}

