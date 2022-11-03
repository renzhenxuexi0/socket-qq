package com.client.controller;

import com.client.application.RegisterApplication;
import com.client.utils.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginInterfaceController {
    @FXML
    private Button loginButton;

    @FXML
    private TextField loginInput;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField registerInput;

    @FXML
    void loginButtonEvent(ActionEvent event) {

    }

    @FXML
    void registerButtonEvent(ActionEvent event) throws Exception {
        RegisterApplication registerApplication = new RegisterApplication();
        registerApplication.start(new Stage());
        StageManager.jump("登录界面", "注册界面");
    }
}
