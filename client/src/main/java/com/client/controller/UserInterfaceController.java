package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.ClientApp;
import com.client.pojo.*;
import com.client.service.FileMsgService;
import com.client.service.UserService;
import com.client.utils.*;
import com.client.view.ChatView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@Slf4j
@EnableScheduling
public class UserInterfaceController implements Initializable, ApplicationContextAware {

    @FXML
    public Button minWindow;
    @FXML
    public Button closeWindow;
    @FXML
    public ImageView backgroundImage;

    @Autowired
    private FileMsgService fileMsgService;

    @Autowired
    private ChatInterfaceController chatInterface;

    private ApplicationContext applicationContext;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @FXML
    private Pane userPane;

    private File textMsgLog;
    private File fileMsgLog;

    @Value("${client.port}")
    private Integer clientPort;

    @FXML
    private Label userName;
    @FXML
    private ImageView userHead;
    @FXML
    private ListView<User> userListView;
    private Stage primaryStage;

    private Image headImage;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0/2 * * * * ?")
    void buildUserList() {
        if (UserMemory.users != null) {
            ObservableList<User> userList = FXCollections.observableArrayList(UserMemory.users);
            Platform.runLater(() -> userListView.setItems(userList));
        }
    }

    @Scheduled(cron = "0/2 * * * * ?")
    void updateMsgListView() {
        if (chatInterface.primaryStage.isShowing() && MsgMemory.sendMsgList != null) {
            setUpMsgListView(headImage, getClass().getResource("fileImage/unknownFile.png"));
            Platform.runLater(() -> {
                MsgMemory.sendMsgListSort(simpleDateFormat);
                chatInterface.msgListView.setItems(FXCollections.observableArrayList(MsgMemory.sendMsgList));
            });
        }
    }

    private void setUpMsgListView(Image headImage, URL resource) {
        MsgMemory.sendMsgList = new ArrayList<>();
        UserMemory.textMsgList.forEach(textMsg -> {
            if ((UserMemory.myUser.getId().equals(textMsg.getSenderId()) &&
                    UserMemory.talkUser.getId().equals(textMsg.getReceiverId()))
                    || UserMemory.talkUser.getId().equals(textMsg.getSenderId())) {
                SendMsg sendMsg = new SendMsg();
                sendMsg.setImage(headImage);
                sendMsg.setMsg(textMsg);
                sendMsg.setType(0);
                MsgMemory.sendMsgList.add(sendMsg);
            }
        });

        UserMemory.fileMsgList.forEach(fileMsg -> {
            if (UserMemory.talkUser.getId().equals(fileMsg.getSenderId())) {
                SendMsg sendMsg = new SendMsg();
                sendMsg.setMsg(fileMsg);
                sendMsg.setImage(headImage);
                sendMsg.setType(1);
                FileMsgVBox fileMsgVBox = new FileMsgVBox();
                sendMsg.setVBox(fileMsgVBox.fileMsgVBox(false, fileMsg.getFileName()));
                fileMsgVBox.setFileImage(new Image(String.valueOf(resource)));
                fileMsgVBox.setHyperlink1OnAction(event1 -> threadPoolExecutor.execute(() -> {
                    Socket socket = fileMsgService.sendOfflineFileMsg();
                    if (socket != null) {
                        try {
                            PrintStream socketPrintStream = new PrintStream(socket.getOutputStream());
                            Result result1 = new Result();
                            result1.setCode(Code.RECEIVE_OFFLINE_FILE_MSG);
                            result1.setObject(fileMsg);
                            socketPrintStream.println(JSON.toJSONString(result1));
                            System.out.println(result1);
                            File file = new File(System.getProperty("user.home") + "\\.socket\\downloadFile\\" + fileMsg.getFileName());
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
                                int len;
                                long accumulationSize = 0L;
                                while ((len = dataInputStream.read(bytes)) != -1) {
                                    rw.write(bytes, 0, len);
                                    accumulationSize += len;
                                    double schedule = (double) accumulationSize / (double) (length);
                                    Platform.runLater(() -> fileMsgVBox.setProgressBarProgress(schedule));
                                }
                                if (accumulationSize == length) {
                                    fileMsgVBox.setProgressBarState("接受完成");
                                    WritableImage fileIcon = GetFileIcon.getFileIcon(file);
                                    Platform.runLater(() -> fileMsgVBox.setFileImage(fileIcon));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }));

                fileMsgVBox.setHyperlink2OnAction(event12 -> {
                    try {
                        Desktop.getDesktop().open(new File("System.getProperty(user.home)" + "\\.socket\\downloadFile"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                MsgMemory.sendMsgList.add(sendMsg);
            } else if (UserMemory.myUser.getId().equals(fileMsg.getSenderId()) &&
                    UserMemory.talkUser.getId().equals(fileMsg.getReceiverId())) {
                creatSendMsg(headImage, fileMsg);
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FontIcon fontIcon = new FontIcon(FontAwesome.SEARCH);
        fontIcon.setIconSize(12);
        fontIcon.setIconColor(Color.WHITE);
        headImage = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));


        ChatView view = applicationContext.getBean(ChatView.class);
        chatInterface.primaryStage = ShowNewViewUtil.showView(view, primaryStage);
        chatInterface.msgListView.setCellFactory(param -> new MsgCell());
        DragUtil.addDragListener(chatInterface.primaryStage, chatInterface.headPane);

        textMsgLog = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\textMsgLog.txt");
        fileMsgLog = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\fileMsgLog.txt");
        File textMsgLogParentFile = textMsgLog.getParentFile();
        File fileMsgLogParentFile = fileMsgLog.getParentFile();

        if (!textMsgLog.exists()) {
            try {
                if (!textMsgLogParentFile.exists()) {
                    textMsgLogParentFile.mkdirs();
                }
                textMsgLog.createNewFile();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }

        if (!fileMsgLog.exists()) {
            try {
                if (!fileMsgLogParentFile.exists()) {
                    fileMsgLogParentFile.mkdirs();
                }
                fileMsgLog.createNewFile();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }

        userListView.setCellFactory(param -> new UserCell() {
            @Override
            public EventHandler<? super MouseEvent> setOnclickBox() {
                Image image = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));
                return (EventHandler<MouseEvent>) event -> {
                    if (event.getClickCount() == 2 && event.getButton().name().equals("PRIMARY")) {
                        chatInterface.inputArea.setText("");
                        UserMemory.talkUser = userListView.getSelectionModel().getSelectedItem();
                        chatInterface.userName.setText(userListView.getSelectionModel().getSelectedItem().getUsername());

                        setUpMsgListView(image, getClass().getResource("fileImage/unknownFile.png"));

                        if (textMsgLog.exists()) {
                            try {
                                String[] jsons1 = FileUtils.readFileToString(textMsgLog, "UTF-8").split("\n");
                                for (String json : jsons1) {
                                    TextMsg textMsg = JSON.parseObject(json, TextMsg.class);
                                    if (Objects.equals(textMsg.getReceiverId(), UserMemory.talkUser.getId())) {
                                        SendMsg sendMsg = new SendMsg();
                                        sendMsg.setImage(image);
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
                                String[] jsons2 = FileUtils.readFileToString(fileMsgLog, "UTF-8").split("\n");
                                for (String json : jsons2) {
                                    FileMsg fileMsg = JSON.parseObject(json, FileMsg.class);
                                    if (Objects.equals(fileMsg.getReceiverId(), UserMemory.talkUser.getId())) {
                                        creatSendMsg(image, fileMsg);
                                    }
                                }
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                        }

                        MsgMemory.sendMsgListSort(simpleDateFormat);
                        chatInterface.msgListView.setItems(FXCollections.observableArrayList(MsgMemory.sendMsgList));

                        chatInterface.primaryStage.show();
                    }
                };
            }
        });

        userName.setText(UserMemory.myUser.getUsername());
        Image image = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));
        userHead.setImage(image);
        buildUserList();
        primaryStage = ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */

        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* 关闭程序 */

        DragUtil.addDragListener(primaryStage, Collections.singletonList(userPane));


        threadPoolExecutor.execute(() -> {
            try (ServerSocket serverSocket = new ServerSocket(clientPort)) {
                // 判读线程是否调用Interrupted，给线程打上中止标记 打上就退出循环
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    threadPoolExecutor.execute(() -> {
                        // 流的封装
                        OutputStream os;//字节输出流抽象类
                        try {
                            os = socket.getOutputStream();
                            PrintStream ps = new PrintStream(os);
                            InputStream is = socket.getInputStream();//面向字节的输入流抽象类
                            Reader reader = new InputStreamReader(is);//创建面向字节的字符流
                            BufferedReader bfr = new BufferedReader(reader);//从字符流中读取文本，缓存

                            JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                            // 数据传给服务层
                            Integer code = Integer.valueOf(jsonObject.getString("code"));

                            if (Code.SEND_TEXT_MSG.equals(code)) {
                                TextMsg textMsg = JSON.parseObject(jsonObject.getString("object"), TextMsg.class);
                                UserMemory.textMsgList.add(textMsg);
                                Result result2 = new Result();
                                result2.setCode(Code.SEND_TEXT_MSG_SUCCESS);
                                ps.println(JSON.toJSONString(result2));
                            } else if (Code.SEND_FILE_MSG.equals(code)) {

                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void creatSendMsg(Image image, FileMsg fileMsg) {
        SendMsg sendMsg = new SendMsg();
        sendMsg.setImage(image);
        sendMsg.setMsg(fileMsg);
        sendMsg.setType(1);
        FileMsgVBox fileMsgVBox = new FileMsgVBox();
        sendMsg.setVBox(fileMsgVBox.fileMsgVBox(true, fileMsg.getFileName()));
        fileMsgVBox.setFileImage(new Image(String.valueOf(getClass().getResource("fileImage/unknownFile.png"))));
        Hyperlink hyperlink1 = fileMsgVBox.getHyperlink1();
        hyperlink1.setText("");
        hyperlink1.setVisited(true);
        Hyperlink hyperlink2 = fileMsgVBox.getHyperlink2();
        hyperlink2.setText("");
        hyperlink2.setVisited(true);
        fileMsgVBox.setProgressBarProgress(100);
        fileMsgVBox.setProgressBarState("已经发送完离线信息");
        MsgMemory.sendMsgList.add(sendMsg);
    }
}
