package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientApp;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.utils.UserMemory;
import com.client.view.RegisterView;
import com.client.view.UserView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@Slf4j
public class LoginInterfaceController implements Initializable {

    @FXML
    public ImageView backgroundImage;
    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;

    @FXML
    public ImageView logoImage;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadPoolExecutor poolExecutor;

    private Stage primaryStage;

    @FXML
    private JFXButton loginButton;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private JFXTextField accountInput;

    @FXML
    private Hyperlink registerHyperlink;

    @FXML
    private JFXPasswordField passwordInput;


    @FXML
    void loginButtonEvent(ActionEvent event) {
        String account = accountInput.getText();
        String password = passwordInput.getText();
        if (!"".equals(account) && !"".equals(password)) {
            try {

                Result result = new Result();
                result.setCode(Code.USER_LOGIN);
                User user = new User();

                Result result2 = poolExecutor.submit(new Callable<Result>() {
                    @Override
                    // 自动抛出错误
                    @SneakyThrows
                    public Result call() throws Exception {
                        user.setAccount(account);
                        user.setPassword(password);
                        result.setObject(user);
                        Result result21 = userService.userLogin(result);
                        UserMemory.users = JSON.parseArray(result21.getObject().toString(), User.class);
                        return result21;
                    }
                }).get();

                // 判断账号密码是否正确
                if (Code.LOGIN_SUCCESS.equals(result2.getCode())) {
                    primaryStage.setHeight(620);
                    primaryStage.setWidth(306);
                    primaryStage.setTitle("IMO");
                    ClientApp.showView(UserView.class);
                    UserMemory.users.forEach(user1 -> {
                        if (user1.getAccount().equals(user.getAccount())) {
                            UserMemory.user = user1;
                        }
                    });

                } else {
                    // 登录失败 弹出错误窗口
                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                    alert.show();
                    // 错误的话得重新输入
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误");
                alert.show();
                log.error(e.toString());
            }

        } else if ("".equals(account)) {
            accountInput.validate();
        } else {
            passwordInput.validate();
        }
    }

    @FXML
    void registerEvent(ActionEvent event) {
        primaryStage.setHeight(412);
        primaryStage.setWidth(400);
        primaryStage.setTitle("注册");
        ClientApp.showView(RegisterView.class);
    }

    // 界面初始化
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountInput.requestFocus();
        RequiredFieldValidator accountValidator = new RequiredFieldValidator();

        FontIcon fontIcon = new FontIcon(FontAwesome.EXCLAMATION_TRIANGLE);
        fontIcon.setIconColor(Color.RED);
        fontIcon.setIconSize(11);

        accountValidator.setIcon(fontIcon);
        accountValidator.setMessage("请输入账户");
        accountInput.getValidators().add(accountValidator);
        accountInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                accountInput.validate();
            }
        });

        RequiredFieldValidator passwordValidator = new RequiredFieldValidator();
        passwordValidator.setMessage("请输入密码");
        passwordValidator.setIcon(fontIcon);

        passwordInput.getValidators().add(passwordValidator);
        passwordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordInput.validate();
            }
        });

        FontIcon fontIcon1 = new FontIcon(FontAwesome.QQ);
        fontIcon1.setIconColor(Color.WHITE);
        fontIcon1.setTranslateX(-50);
        fontIcon1.setIconSize(13);
        loginButton.setGraphic(fontIcon1);

        primaryStage = ClientApp.getStage(); //primaryStage为start方法头中的Stage

        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */

        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Arrays.asList(backgroundImage, logoImage));
    }
}

