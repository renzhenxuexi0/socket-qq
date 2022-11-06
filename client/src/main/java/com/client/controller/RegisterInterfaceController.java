package com.client.controller;

import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegisterInterfaceController {
    private final UserService userService = new UserService();
    @FXML
    private TextField accountInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private Button submitButton;

    @FXML
    private TextField userNameInput;

    /**
     * 提交按钮触发事件
     *
     * @param event
     */
    @FXML
    void submitButtonEvent(ActionEvent event) throws Exception {
        Data data = new Data();
        data.setCode(Code.USER_REGISTER);
        User user = new User();
        user.setAccount(accountInput.getText());
        user.setPassword(passwordInput.getText());
        user.setUsername(userNameInput.getText());
        user.setLogin(0);
        data.setObject(user);
        Data data2 = userService.userRegister(data);
        Alert alert;
        if (Code.REGISTER_SUCCESS.equals(data2.getCode())) {
            // 注册成功弹窗 显示服务器返回的信息
            alert = new Alert(Alert.AlertType.INFORMATION, data2.getMsg());
            alert.showAndWait();
            StageManager.jump("注册界面", "登录界面");
        } else {
            // 注册失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, data2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }
}
