package com.client.controller;

import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.view.LoginView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@FXMLController
public class RegisterInterfaceController implements Initializable {
    @FXML
    private ImageView rgBackround;
    @FXML
    private ImageView rgLogo;
    @Autowired
    private UserService userService;
    @FXML
    private JFXTextField accountInput;
    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    private Stage primaryStage;
    // 账户验证

    @FXML
    private JFXTextField passwordInput;

    @FXML
    private JFXButton submitButton;

    @FXML
    private JFXTextField userNameInput;
    @FXML
    private Hyperlink returnLogin;
    @FXML
    void jumpLogin(ActionEvent event) {
        primaryStage.setHeight(344);
        primaryStage.setWidth(428);
        primaryStage.setTitle("登录");
        ClientApp.showView(LoginView.class);

    }

    /**
     * 提交按钮触发事件
     */
    @FXML
    void submitButtonEvent() {
        Result result = new Result();
        result.setCode(Code.USER_REGISTER);
        User user = new User();
        String account = accountInput.getText();
        String password = passwordInput.getText();
        String username = userNameInput.getText();
        user.setAccount(account);
        user.setPassword(password);
        user.setUsername(username);
        user.setLogin(0);
        result.setObject(user);
        Result result2 = userService.userRegister(result);
        Alert alert;

        if (!"".equals(account) && !"".equals(password)&&!"".equals(username)) {
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
        }else if ("".equals(account)) {
            accountInput.validate();
        } else if ("".equals(password)){
            passwordInput.validate();
        }else {
            userNameInput.validate();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userNameInput.requestFocus();
        RequiredFieldValidator userNameValidator = new RequiredFieldValidator();

        FontIcon fontIcon = new FontIcon(FontAwesome.TIMES_CIRCLE_O);
        fontIcon.setIconColor(Color.RED);
        fontIcon.setIconSize(11);
        userNameValidator.setIcon(fontIcon);
        userNameValidator.setMessage("用户名格式有误");
        userNameInput.getValidators().add(userNameValidator);
        userNameInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                userNameInput.validate();
            }
        });

        RequiredFieldValidator passwordValidator = new RequiredFieldValidator();
        passwordValidator.setIcon(fontIcon);
        passwordValidator.setMessage("密码格式有误");
        passwordInput.getValidators().add(passwordValidator);
        passwordInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                passwordInput.validate();
            }
        });
        RequiredFieldValidator accountValidator = new RequiredFieldValidator();
        accountValidator.setIcon(fontIcon);
        accountValidator.setMessage("账号格式有误");
        accountInput.getValidators().add(accountValidator);
        accountInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                accountInput.validate();
            }
        });
        primaryStage=ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction((event) -> System.exit(0)); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Arrays.asList(rgBackround, rgLogo));
    }
}


