package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.utils.ProgressStageUtil;
import com.client.utils.UserMemory;
import com.client.view.LoginView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@Slf4j
public class RegisterInterfaceController implements Initializable {
    @Autowired
    private ThreadPoolExecutor poolExecutor;
    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @FXML
    private ImageView rgBackround;
    @FXML
    private ImageView rgLogo;
    @Autowired
    private UserService userService;
    @FXML
    private JFXTextField accountInput;
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

        String account = accountInput.getText();
        String password = passwordInput.getText();
        String username = userNameInput.getText();


        if (!"".equals(account) && !"".equals(password) && !"".equals(username)) {
            Task<Void> task = new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    try {
                        Result result = new Result();
                        result.setCode(Code.USER_REGISTER);
                        User user = new User();

                        Result result2 = poolExecutor.submit(() -> {
                            user.setAccount(account);
                            user.setPassword(password);
                            user.setUsername(username);
                            user.setLogin(0);
                            result.setObject(user);
                            Result result3 = null;
                            try {
                                result3 = userService.userRegister(result);
                                UserMemory.users = JSON.parseArray(result3.getObject().toString(), User.class);
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                            return result3;
                        }).get();
                        Platform.runLater(() -> {
                            try {
                                if (Code.REGISTER_SUCCESS.equals(result2.getCode())) {
                                    // 注册成功弹窗 显示服务器返回的信息
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, result2.getMsg());
                                    ClientApp.showView(LoginView.class);
                                    alert.showAndWait();
                                } else {
                                    // 注册失败 弹出错误窗口
                                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                    alert.show();
                                    // 错误的话得重新输入
                                }
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                        });
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误");
                        alert.show();
                        log.error(e.toString());
                    }
                    return null;
                }
            };
            ProgressStageUtil.of(primaryStage, poolExecutor, task, "注册中").show();
        } else if ("".equals(account)) {
            accountInput.validate();
        } else if ("".equals(password)) {
            passwordInput.validate();
        } else {
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
        primaryStage = ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Arrays.asList(rgBackround, rgLogo));
    }
}


