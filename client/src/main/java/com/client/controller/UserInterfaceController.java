package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientApp;
import com.client.pojo.SendMsg;
import com.client.pojo.TextMsg;
import com.client.pojo.User;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.utils.MsgMemory;
import com.client.utils.ShowNewViewUtil;
import com.client.utils.UserMemory;
import com.client.view.ChatView;
import com.jfoenix.controls.JFXTextField;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ResourceBundle;

@FXMLController
@Slf4j
public class UserInterfaceController implements Initializable, ApplicationContextAware {

    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @FXML
    public ImageView backgroundImage;
    @FXML
    public JFXTextField findUserTextField;
    @Autowired
    private ChatInterfaceController chatInterface;

    private ApplicationContext applicationContext;

    private Stage newStage;
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @FXML
    private Pane userPane;

    private File msgFile;

    @FXML
    private Label userName;
    @FXML
    private ImageView userHead;
    @FXML
    private ListView<User> userListView;
    private Stage primaryStage;

    @Autowired
    private UserService userService;

    void buildUserList() {
        ObservableList<User> userList = FXCollections.observableArrayList(UserMemory.users);
        userListView.setItems(userList);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FontIcon fontIcon = new FontIcon(FontAwesome.SEARCH);
        fontIcon.setIconSize(12);
        fontIcon.setIconColor(Color.WHITE);

        msgFile = new File(System.getProperty("user.home") + "\\.socket\\msg.txt");

        userListView.setCellFactory(param -> new UserCell() {
            @Override
            public EventHandler<? super MouseEvent> setOnclickBox() {
                return (EventHandler<MouseEvent>) event -> {
                    if (event.getClickCount() == 2 && event.getButton().name().equals("PRIMARY")) {
                        ChatView view = applicationContext.getBean(ChatView.class);
                        chatInterface.primaryStage = ShowNewViewUtil.showView(view, primaryStage);
                        chatInterface.inputArea.setText("");

                        UserMemory.talkUser = userListView.getSelectionModel().getSelectedItem();
                        chatInterface.userName.setText(userListView.getSelectionModel().getSelectedItem().getUsername());


                        UserMemory.textMsgList.forEach(textMsg -> {
                            if (UserMemory.talkUser.getId().equals(textMsg.getSenderId())) {
                                SendMsg sendMsg = new SendMsg();
                                sendMsg.setMsg(textMsg);
                                sendMsg.setType(0);
                                MsgMemory.sendMsgList.add(sendMsg);
                            }
                        });

                        if (msgFile.exists()) {
                            try {
                                String[] jsons = FileUtils.readFileToString(msgFile, "UTF-8").split("\n");
                                for (int i = 0; i < jsons.length; i++) {
                                    TextMsg textMsg = JSON.parseObject(jsons[i], TextMsg.class);
                                    if (UserMemory.talkUser.getId().equals(textMsg.getReceiveId())) {
                                        SendMsg sendMsg = new SendMsg();
                                        sendMsg.setMsg(textMsg);
                                        sendMsg.setType(0);
                                        MsgMemory.sendMsgList.add(sendMsg);
                                    }
                                }
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                        }

                        MsgMemory.sendMsgListSort(simpleDateFormat);

                        chatInterface.msgListView.setCellFactory(param -> new MsgCell());
                        chatInterface.msgListView.setItems(FXCollections.observableArrayList(MsgMemory.sendMsgList));

                        DragUtil.addDragListener(chatInterface.primaryStage, chatInterface.headPane);
                        chatInterface.primaryStage.show();
                    }
                };
            }
        });

        userName.setText(UserMemory.myUser.getUsername());
        Image image = new Image(String.valueOf(getClass().getResource("headImage/head.png")));
        userHead.setImage(image);
        buildUserList();
        primaryStage = ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */

        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Collections.singletonList(userPane));
        // 开启定时任务
//        ScheduledService<Void> scheduledService = new ScheduledService<Void>() {
//            @Override
//            protected Task<Void> createTask() {
//                buildUserList();
//                return null;
//            }
//        };
//        scheduledService.setPeriod(Duration.seconds(5));
//        scheduledService.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
