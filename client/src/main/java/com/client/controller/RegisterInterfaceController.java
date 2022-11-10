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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class RegisterInterfaceController implements Initializable {
    @Autowired
    private UserService userService;
    @FXML
    private TextField accountInput;

    // 账户验证
    @FXML
    private Label av;
    //用户名验证
    @FXML
    private Label uv;
    //密码验证
    @FXML
    private Label pv;

    @FXML
    private TextField passwordInput;

    @FXML
    private Button submitButton;

    @FXML
    private TextField userNameInput;

    /**
     * 提交按钮触发事件
     *
     * @param
     */
    @FXML
    void submitButtonEvent() {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountInput.setPromptText("请输入6-11位数字");
        passwordInput.setPromptText("请输入任意6-11位数字或英文");
        userNameInput.setPromptText("请输入6位以内英文或数字");
    }

    public void accountVerification(MouseEvent event) {
        String account = accountInput.getText();

        if (account.matches("[^0]\\d{5,10}")) {
            submitButton.setOnAction(event1 -> submitButtonEvent());
            av.setText("");
        } else if ("".equals(account)) {
            submitButton.setOnAction(event1 -> {});
            av.setText("");
        } else {
            submitButton.setOnAction(event1 -> {});
            av.setText("账号错误");
        }
    }



    public void usernameVerification(MouseEvent event) {
        String username = userNameInput.getText();
        if (username.matches("[A-Za-z0-9]{0,5}")){
            submitButton.setOnAction(event1 -> submitButtonEvent());
            uv.setText("");
        }else if ("".equals(username)) {
            submitButton.setOnAction(event1 -> {});
            uv.setText("");
        } else {
            submitButton.setOnAction(event1 -> {});
            uv.setText("用户名错误");
        }
    }

    public void passwordVerification(MouseEvent event) {
        String password = passwordInput.getText();
        if (password.matches("[A-Za-z0-9]{5,10}")){
            submitButton.setOnAction(event1 -> submitButtonEvent());
            pv.setText("");
        }else if ("".equals(password)) {
            submitButton.setOnAction(event1 -> {});
            pv.setText("");
        } else {
            submitButton.setOnAction(event1 -> {});
            pv.setText("密码错误");
        }
    }
}
