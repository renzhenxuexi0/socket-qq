package com.client.controller;

import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class RegisterInterfaceController {
    @Autowired
    private UserService userService;
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
    void submitButtonEvent(ActionEvent event) {
        Result result = new Result();
        result.setCode(Code.USER_REGISTER);
        User user = new User();
        user.setAccount(accountInput.getText());
        user.setPassword(passwordInput.getText());
        user.setUsername(userNameInput.getText());
        user.setLogin(0);
        result.setObject(user);
        Result result2 = userService.userRegister(result);
        Alert alert;
        if (Code.REGISTER_SUCCESS.equals(result2.getCode())) {
            // 注册成功弹窗 显示服务器返回的信息
            alert = new Alert(Alert.AlertType.INFORMATION, result2.getMsg());
            ClientApp.showView(UserView.class, Modality.APPLICATION_MODAL);
            alert.showAndWait();
        } else {
            // 注册失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }
}
