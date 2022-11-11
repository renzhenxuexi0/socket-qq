package com.client.controller;

import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.view.LoginView;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

@FXMLController
public class RegisterInterfaceController implements Initializable {
    private boolean AV,UV,PV;
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
            ClientApp.showView(LoginView.class);
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
        submitButton.setDisable(true);
        userNameInput.setPromptText("请输入6位以内英文或数字");
        userNameInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[A-Za-z0-9]{0,6}")) {
                uv.setText("");
                UV=true;
                submitButton.setDisable(false);
            } else if ("".equals(newValue)) {
                uv.setText("用户名不能为空");
                submitButton.setDisable(true);
            } else {
                uv.setText("用户名错误");
                submitButton.setDisable(true);
            }
        });
        passwordInput.setPromptText("请输入任意6-11位数字或英文");
        passwordInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[A-Za-z0-9]{6,11}")) {
                pv.setText("");
                PV=true;
                submitButton.setDisable(false);
            } else if ("".equals(newValue)) {
                pv.setText("密码不能为空");
                submitButton.setDisable(true);
            } else {
                pv.setText("密码错误");
                submitButton.setDisable(true);
            }
        });
        accountInput.setPromptText("请输入6-11位数字");
        accountInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("[^0]\\d{5,10}")) {
                av.setText("");
                AV=true;
                submitButton.setDisable(false);
            } else if ("".equals(newValue)) {
                av.setText("账号不能为空");
                submitButton.setDisable(true);
            } else {
                av.setText("账号错误");
                submitButton.setDisable(true);
            }
        });
    }
}


