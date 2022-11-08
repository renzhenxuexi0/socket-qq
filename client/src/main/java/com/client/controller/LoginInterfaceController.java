package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientMain;
import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import com.client.view.RegisterView;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;

@FXMLController
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
    void loginButtonEvent(ActionEvent event) throws Exception {
        Data data = new Data();
        data.setCode(Code.USER_LOGIN);
        User user = new User();
        user.setAccount(accountInput.getText());
        user.setPassword(passwordInput.getText());
        data.setObject(user);
        Data data2 = userService.userLogin(data);
        UserMemory.users = JSON.parseArray(data2.getObject().toString(), User.class);
        Alert alert;
        if (Code.LOGIN_SUCCESS.equals(data2.getCode())) {
            ClientMain.showView(UserView.class, Modality.APPLICATION_MODAL);
            System.out.println("success！");
        } else {
            // 登录失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, data2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }

    @FXML
    void registerButtonEvent(ActionEvent event) throws Exception {
        ClientMain.showView(RegisterView.class, Modality.APPLICATION_MODAL);
    }
}

