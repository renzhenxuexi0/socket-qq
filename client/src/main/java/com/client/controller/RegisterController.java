package com.client.controller;

import com.client.pojo.User;
import com.client.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegisterController {
    private UserService userService = new UserService();
    @FXML
    private TextField accountInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private Button submitButton;

    @FXML
    private TextField userNameInput;

    @FXML
    void submitButtonEvent(ActionEvent event) {
        User user = new User();
        user.setAccountNumber(accountInput.getText());
        user.setPassword(passwordInput.getText());
        user.setUsername(userNameInput.getText());
        userService.userRegister(user);
    }
}
