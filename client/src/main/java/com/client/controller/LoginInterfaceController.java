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
import com.jfoenix.controls.*;
import com.jfoenix.validation.RequiredFieldValidator;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class LoginInterfaceController implements Initializable {
    @FXML
    public ImageView backgroundImage;
    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @Autowired
    private UserService userService;
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
        Result result = new Result();
        result.setCode(Code.USER_LOGIN);
        User user = new User();
        String account = accountInput.getText();
        String password = passwordInput.getText();
        if (!"".equals(account) && !"".equals(password)) {
            user.setAccount(account);
            user.setPassword(password);
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
        } else {
            JFXAlert jfxAlert = new JFXAlert(primaryStage);
            jfxAlert.initModality(Modality.APPLICATION_MODAL);
            jfxAlert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setBody(new Label("请输入正确的账号密码"));
            JFXButton closeButton = new JFXButton("确认");
            closeButton.setStyle("-fx-font-size: 14");
            closeButton.getStyleClass().add("dialog-accept");
            closeButton.setOnAction(event2 -> jfxAlert.hideWithAnimation());
            layout.setActions(closeButton);
            jfxAlert.setContent(layout);
            jfxAlert.show();
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
        accountInput.requestFocus();
        RequiredFieldValidator accountValidator = new RequiredFieldValidator();
        accountValidator.setMessage("请输入账号");

        FontIcon fontIcon = new FontIcon(FontAwesome.EXCLAMATION_TRIANGLE);
        fontIcon.setIconColor(Color.RED);
        fontIcon.setIconSize(11);

        accountValidator.setIcon(fontIcon);

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

        passwordInput.setPromptText("输入6~11位的密码");

        primaryStage = ClientApp.getStage(); //primaryStage为start方法头中的Stage
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction((event) -> System.exit(0)); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, backgroundImage);

    }
}

