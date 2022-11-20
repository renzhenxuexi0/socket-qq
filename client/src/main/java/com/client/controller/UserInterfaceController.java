package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.ClientApp;
import com.client.pojo.*;
import com.client.service.FileMsgService;
import com.client.service.UserService;
import com.client.utils.DragUtil;
import com.client.utils.MsgMemory;
import com.client.utils.ShowNewViewUtil;
import com.client.utils.UserMemory;
import com.client.view.ChatView;
import com.jfoenix.controls.JFXTextField;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
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

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;
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
    private FileMsgService fileMsgService;

    @Autowired
    private ChatInterfaceController chatInterface;

    private ApplicationContext applicationContext;

    private Stage newStage;
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @FXML
    private Pane userPane;

    private File textMsgLog;
    private File fileMsgLog;

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

        textMsgLog = new File(System.getProperty("user.home") + "\\.socket\\textMsgLog.txt");
        fileMsgLog = new File(System.getProperty("user.home") + "\\.socket\\fileMsgLog.txt");

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

                        UserMemory.fileMsgList.forEach(fileMsg -> {
                            if (UserMemory.talkUser.getId().equals(fileMsg.getSenderId())) {
                                SendMsg sendMsg = new SendMsg();
                                sendMsg.setMsg(fileMsg);
                                sendMsg.setType(1);
                                FileMsgVBox fileMsgVBox = new FileMsgVBox();
                                sendMsg.setVBox(fileMsgVBox.fileMsgVBox(false, fileMsg.getFileName()));
                                fileMsgVBox.setFileImage(new Image(String.valueOf(getClass().getResource("fileImage/unknownFile.png"))));
                                fileMsgVBox.setHyperlink1OnAction(event1 -> {
                                    Socket socket = fileMsgService.sendOfflineFileMsg();
                                    if (socket != null) {
                                        try {
                                            PrintStream socketPrintStream = new PrintStream(socket.getOutputStream());
                                            Result result1 = new Result();
                                            result1.setCode(Code.RECEIVE_OFFLINE_FILE_MSG);
                                            result1.setObject(fileMsg);
                                            socketPrintStream.println(JSON.toJSONString(result1));
                                            System.out.println(result1);
                                            File file = new File("System.getProperty(user.home)" + "\\.socket\\downloadFile\\" + fileMsg.getFileName());
                                            if (!file.exists()) {
                                                try {
                                                    file.createNewFile();
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }

                                            try (RandomAccessFile rw = new RandomAccessFile(file, "rw")) {
                                                long length = fileMsg.getEndPoint() - fileMsg.getStartPoint();
                                                rw.seek(fileMsg.getStartPoint());
                                                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                                byte[] bytes = new byte[1024 * 10];
                                                int len = 0;
                                                long accumulationSize = 0L;
                                                while ((len = dataInputStream.read(bytes)) != -1) {
                                                    rw.write(bytes, 0, len);
                                                    accumulationSize += len;
                                                    double schedule = (double) accumulationSize / (double) (length);
                                                    Platform.runLater(() -> fileMsgVBox.setProgressBarProgress(schedule));
                                                }
                                                if (accumulationSize == length) {
                                                    fileMsgVBox.setProgressBarState("接受完成");
                                                }
                                                System.out.println("文件传输结束");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                fileMsgVBox.setHyperlink2OnAction(event12 -> {
                                    try {
                                        Desktop.getDesktop().open(new File("System.getProperty(user.home)" + "\\.socket\\downloadFile"));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });

                                MsgMemory.sendMsgList.add(sendMsg);
                            }
                        });

                        if (textMsgLog.exists()) {
                            try {
                                String[] jsons = FileUtils.readFileToString(textMsgLog, "UTF-8").split("\n");
                                for (String json : jsons) {
                                    TextMsg textMsg = JSON.parseObject(json, TextMsg.class);
                                    if (Objects.equals(textMsg.getReceiveId(), UserMemory.talkUser.getId())) {
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

                        if (fileMsgLog.exists()) {
                            try {
                                String[] jsons = FileUtils.readFileToString(fileMsgLog, "UTF-8").split("\n");
                                for (String json : jsons) {
                                    FileMsg fileMsg = JSON.parseObject(json, FileMsg.class);
                                    if (Objects.equals(fileMsg.getReceiveId(), UserMemory.talkUser.getId())) {
                                        SendMsg sendMsg = new SendMsg();
                                        sendMsg.setMsg(fileMsg);
                                        sendMsg.setType(1);
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
