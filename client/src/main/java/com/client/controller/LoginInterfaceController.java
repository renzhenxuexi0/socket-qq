package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.ClientApp;
import com.client.config.ProgressStageConfig;
import com.client.pojo.*;
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
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@Slf4j
public class LoginInterfaceController implements Initializable {

    @FXML
    private ImageView backgroundImage;
    @FXML
    private Button minWindow;
    @FXML
    private Button closeWindow;

    @FXML
    private ImageView logoImage;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadPoolExecutor poolExecutor;

    private File file;

    private Stage primaryStage;
    @Autowired
    private ProgressStageConfig progressStageConfig;

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
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Result result = new Result();
                        result.setCode(Code.USER_LOGIN);
                        User user = new User();

                        Result result2 = poolExecutor.submit(() -> {
                            user.setAccount(account);
                            user.setPassword(password);
                            result.setObject(user);
                            Result result3 = null;
                            try {
                                result3 = userService.userLogin(result);
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                            return result3;
                        }).get();

                        Platform.runLater(() -> {
                            try {
                                // 判断账号密码是否正确
                                if (Code.LOGIN_SUCCESS.equals(result2.getCode())) {

                                    // 解析数据
                                    JSONObject jsonObject = JSON.parseObject(result2.getObject().toString());
                                    UserMemory.myUser = JSON.parseObject(jsonObject.get("myUser").toString(), User.class);
                                    List<User> allUser = JSON.parseArray(jsonObject.get("users").toString(), User.class);
                                    List<User> allUser2 = new ArrayList<>();
                                    allUser.forEach(user2 -> {
                                        if (!user2.getId().equals(UserMemory.myUser.getId())) {
                                            allUser2.add(user2);
                                        }
                                    });
                                    UserMemory.users = allUser2;

                                    UserMemory.textMsgList = JSON.parseArray(jsonObject.get("textMsg").toString(), TextMsg.class);
                                    UserMemory.fileMsgList = JSON.parseArray(jsonObject.get("fileMsg").toString(), FileMsg.class);

                                    // 登录成功保存账户密码
                                    Properties properties = new Properties();
                                    properties.put("user.account", account);
                                    if (rememberCheckBox.isSelected()) {
                                        properties.put("user.password", password);
                                        properties.put("user.select", "1");
                                    } else {
                                        properties.put("user.password", "");
                                        properties.put("user.select", "0");
                                    }
                                    File fileParent = file.getParentFile();
                                    //判断是否存在，如果不存在则创建目录
                                    if (!fileParent.exists()) {
                                        fileParent.mkdirs();
                                        // 创建文件
                                        file.createNewFile();
                                    }
                                    properties.list(new PrintStream(file));

                                    primaryStage.setHeight(650);
                                    primaryStage.setWidth(307);
                                    primaryStage.setTitle(UserMemory.myUser.getUsername() + "的IMO");
                                    ClientApp.showView(UserView.class);
                                } else {
                                    // 登录失败 弹出错误窗口
                                    // 错误的话得重新输入
                                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                    alert.show();
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

            progressStageConfig.setParent(primaryStage);
            progressStageConfig.setText("登陆中");
            progressStageConfig.setWork(task);
            progressStageConfig.show();
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

        file = new File(System.getProperty("user.home") + "\\.socket\\user.properties");

        try {
            File file1 = new File("System.getProperty(user.home)" + "\\.socket\\downloadFile");
            if (!file1.exists()) {
                FileUtils.forceMkdir(file1);
            }
        } catch (IOException e) {
            log.error(e.toString());
        }

        if (file.exists()) {
            try {
                Properties properties = new Properties();
                properties.load(new InputStreamReader(Files.newInputStream(file.toPath())));
                String select = properties.getProperty("user.select");
                String account = properties.getProperty("user.account");
                String password = properties.getProperty("user.password");
                rememberCheckBox.setSelected("1".equals(select));
                accountInput.setText(account);
                if ("1".equals(select)) {
                    passwordInput.setText(password);
                }
            } catch (IOException e) {
                log.error(e.toString());
            }
        }


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

