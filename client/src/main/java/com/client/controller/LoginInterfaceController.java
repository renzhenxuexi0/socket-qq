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
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class LoginInterfaceController implements Initializable {
    @FXML
    public ImageView backgroundImage;
    @Autowired
    private UserService userService;

    private Stage primaryStage;

    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @FXML
    private Label passwordReminder;


    @FXML
    private Label accountReminder;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox rememberCheckBox;

    @FXML
    private TextField accountInput;

    @FXML
    private Hyperlink registerHyperlink;

    @FXML
    private PasswordField passwordInput;


    @FXML
    void loginButtonEvent(ActionEvent event) {
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
            ClientApp.showView(UserView.class);
            System.out.println("success！");
        } else {
            // 登录失败 弹出错误窗口
            alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
            alert.show();
            // 错误的话得重新输入
        }
    }

    @FXML
    void registerEvent(ActionEvent event) {
        primaryStage.setHeight(600);
        primaryStage.setWidth(500);
        primaryStage.setTitle("注册");
        ClientApp.showView(RegisterView.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDisable(true);
        accountInput.requestFocus();
        accountInput.setPromptText("输入6~11位的账号");

        accountInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                if ("".equals(accountInput.getText())) {
                    accountReminder.setText("账号不能为空");
                    loginButton.setDisable(true);
                } else if ("".equals(passwordInput.getText())){
                    accountReminder.setText("");
                    loginButton.setDisable(true);
                } else {
                    accountReminder.setText("");
                    loginButton.setDisable(false);
                }
            }
        });
        passwordInput.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (oldValue && !newValue) {
                    if ("".equals(passwordInput.getText())) {
                        passwordReminder.setText("密码不能为空");
                        loginButton.setDisable(true);
                    } else if ("".equals(accountInput.getText())){
                        passwordReminder.setText("");
                        loginButton.setDisable(true);
                    } else {
                        passwordReminder.setText("");
                        loginButton.setDisable(false);
                    }
                }
            }
        });

        passwordInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!"".equals(newValue)) {
                    loginButton.setDisable(false);
                }
            }
        });

        accountInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!"".equals(newValue)) {
                    loginButton.setDisable(false);
                }
            }
        });

        passwordInput.setPromptText("输入6~11位的密码");

        primaryStage = ClientApp.getStage(); //primaryStage为start方法头中的Stage
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction((event) -> System.exit(0)); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, backgroundImage);

    }
}

