package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.UserMemory;
import com.client.view.RegisterView;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class LoginInterfaceController implements Initializable {
    @Autowired
    private UserService userService;

    @FXML
    private Label passwordReminder;


    @FXML
    private Label accountReminder;

    @FXML
    private Button loginButton;

    @FXML
    private TextField accountInput;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordInput;

    @FXML
    void loginButtonEvent() {
        Result result = new Result();
        result.setCode(Code.USER_LOGIN);
        User user = new User();
        user.setAccount(accountInput.getText());
        user.setPassword(passwordInput.getText());
        result.setObject(user);
        Result result2 = userService.userLogin(result);
        UserMemory.users = JSON.parseArray(result2.getObject().toString(), User.class);
        Alert alert;
        if (Code.LOGIN_SUCCESS.equals(result2.getCode())) {
            ClientApp.showView(UserView.class, Modality.APPLICATION_MODAL);
            System.out.println("success！");
        } else {
            // 登录失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }

    @FXML
    void accountInputVerification(MouseEvent event) {
        String account = accountInput.getText();
        // 正则表达式匹配字符
        if (account.matches("[^0]\\d{5,10}$")) {
            loginButton.setOnAction(event1 -> loginButtonEvent());
            accountReminder.setText("");
        } else if ("".equals(account)){
            loginButton.setOnAction(event1 -> {
            });
            accountReminder.setText("");
        }else {
            loginButton.setOnAction(event1 -> {
            });
            accountReminder.setText("账号输入错误");
        }
    }

    @FXML
    void passwordInputVerification(MouseEvent event) {
        String password = passwordInput.getText();
        if (password.matches("[a-zA-Z0-9]{6,11}")) {
            loginButton.setOnAction(event1 -> loginButtonEvent());
            passwordReminder.setText("");
        } else if ("".equals(password)){
            loginButton.setOnAction(event1 -> {
            });
            passwordReminder.setText("");
        }else {
            loginButton.setOnAction(event1 -> {
            });
            passwordReminder.setText("密码输入错误");
        }
    }

    @FXML
    void registerButtonEvent(ActionEvent event) {
        Stage stage = ClientApp.getStage();
        stage.setHeight(600);
        stage.setWidth(500);
        stage.setTitle("注册界面");
        ClientApp.showView(RegisterView.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountInput.setPromptText("输入6~11位的账号");
        passwordInput.setPromptText("输入6~11位的密码");
    }
}

