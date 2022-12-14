package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.client.ClientApp;
import com.client.pojo.*;
import com.client.service.FileMsgService;
import com.client.service.TextMsgService;
import com.client.service.UserService;
import com.client.utils.*;
import com.client.view.ChatView;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.Future;
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
    @FXML
    public JFXTextField findUserTextField;

    @Autowired
    private FileMsgService fileMsgService;

    @Autowired
    private TextMsgService textMsgService;
    @Autowired
    private ChatInterfaceController chatInterface;

    private ApplicationContext applicationContext;

    private Stage newStage;
    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Autowired
    private ThreadPoolExecutor poolExecutor;
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

    private FileChooser fileChooser;

    @Scheduled(cron = "0/2 * * * * ?")
    void buildUserList() {
        if (UserMemory.users != null) {
            ObservableList<User> userList = FXCollections.observableArrayList(UserMemory.users);
            Platform.runLater(() -> userListView.setItems(userList));
        }
    }

    private void setUpMsgListView(Image headImage, URL resource) {
        UserMemory.textMsgList.forEach(textMsg -> {
            try {
                if (UserMemory.myUser.getId().equals(textMsg.getSenderId()) && UserMemory.talkUser.getId().equals(textMsg.getReceiverId())) {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                    Label userName = (Label) root.lookup("#userName");
                    Label sendContent = (Label) root.lookup("#sendContent");
                    Label sendTime = (Label) root.lookup("#sendTime");
                    ImageView senderImage = (ImageView) root.lookup("#senderImage");

                    userName.setText(UserMemory.myUser.getUsername());
                    sendContent.setText(textMsg.getContent());
                    sendContent.setStyle("-fx-background-color:  #95EC69; -fx-border-radius: 45; -fx-background-radius: 45;");

                    sendTime.setText(textMsg.getMessageTime());
                    senderImage.setImage(headImage);

                    root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));
                } else if (UserMemory.myUser.getId().equals(textMsg.getReceiverId()) && UserMemory.talkUser.getId().equals(textMsg.getSenderId())) {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                    Label userName = (Label) root.lookup("#userName");
                    Label sendTime = (Label) root.lookup("#sendTime");
                    Label sendContent = (Label) root.lookup("#sendContent");
                    ImageView senderImage = (ImageView) root.lookup("#senderImage");

                    userName.setText(UserMemory.talkUser.getUsername());
                    sendTime.setText(textMsg.getMessageTime());
                    senderImage.setImage(headImage);
                    sendContent.setText(textMsg.getContent());

                    Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        UserMemory.fileMsgList.forEach(fileMsg -> {
            try {
                if (UserMemory.myUser.getId().equals(fileMsg.getReceiverId()) && UserMemory.talkUser.getId().equals(fileMsg.getSenderId())) {
                    Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                    Label userName = (Label) root.lookup("#userName");
                    Label sendTime = (Label) root.lookup("#sendTime");
                    Label sendContent = (Label) root.lookup("#sendContent");
                    ImageView senderImage = (ImageView) root.lookup("#senderImage");

                    FileMsgVBox fileMsgVBox = new FileMsgVBox();
                    if (Objects.equals(fileMsg.getSign(), 1)) {
                        sendContent.setGraphic(fileMsgVBox.fileMsgVBox(true, fileMsg.getFileName()));
                        sendContent.setGraphicTextGap(0);
                        fileMsgVBox.setFileImage(new Image(String.valueOf(resource)));
                        fileMsgVBox.setProgressBarState("?????????????????????");
                    } else {
                        sendContent.setGraphic(fileMsgVBox.fileMsgVBox(false, fileMsg.getFileName()));
                        sendContent.setGraphicTextGap(0);
                        fileMsgVBox.setFileImage(new Image(String.valueOf(resource)));
                        fileMsgVBox.setHyperlink1OnAction(event1 -> poolExecutor.execute(() -> {
                            Socket socket = fileMsgService.sendOfflineFileMsg();
                            if (socket != null) {
                                try {
                                    PrintStream socketPrintStream = new PrintStream(socket.getOutputStream());
                                    Result result1 = new Result();
                                    result1.setCode(Code.RECEIVE_OFFLINE_FILE_MSG);
                                    result1.setObject(fileMsg);
                                    socketPrintStream.println(JSON.toJSONString(result1));

                                    File file = new File(System.getProperty("user.home") + "\\.socket\\downloadFile\\" + fileMsg.getFileName());
                                    if (!file.exists()) {
                                        try {
                                            File parentFile = file.getParentFile();
                                            if (!parentFile.exists()) {
                                                parentFile.mkdirs();
                                            }
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    try (RandomAccessFile rw = new RandomAccessFile(file, "rw")) {
                                        long length = fileMsg.getSize();
                                        rw.seek(fileMsg.getStartPoint());
                                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                                        byte[] bytes = new byte[(int) (fileMsg.getSize() / 5 >= 1 ? fileMsg.getSize() / 5 : 1)];
                                        int len;
                                        long accumulationSize = 0L;
                                        while ((len = dataInputStream.read(bytes)) != -1) {
                                            rw.write(bytes, 0, len);
                                            accumulationSize += len;
                                            double schedule = (double) accumulationSize / (double) (length);
                                            Platform.runLater(() -> fileMsgVBox.setProgressBarProgress(schedule));
                                            if (accumulationSize == length) {
                                                fileMsgVBox.setProgressBarState("????????????");
                                                WritableImage fileIcon = GetFileIcon.getFileIcon(file);
                                                Platform.runLater(() -> fileMsgVBox.setFileImage(fileIcon));
                                                break;
                                            }
                                        }
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }));
                    }

                    fileMsgVBox.setHyperlink2OnAction(event2 -> {
                        try {
                            Desktop.getDesktop().open(new File(System.getProperty("user.home") + "\\.socket\\downloadFile"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    userName.setText(UserMemory.talkUser.getUsername());
                    sendTime.setText(fileMsg.getMessageTime());
                    senderImage.setImage(headImage);

                    Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));
                } else if (UserMemory.myUser.getId().equals(fileMsg.getSenderId()) && UserMemory.talkUser.getId().equals(fileMsg.getReceiverId())) {
                    creatSendMsg(fileMsg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();
        fileChooser.setTitle("???????????????????????????");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All File", "*.*"));

        FontIcon fontIcon = new FontIcon(FontAwesome.SEARCH);
        fontIcon.setIconSize(12);
        fontIcon.setIconColor(Color.WHITE);
        headImage = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));


        ChatView view = applicationContext.getBean(ChatView.class);
        chatInterface.primaryStage = ShowNewViewUtil.showView(view, primaryStage);
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
                        chatInterface.msgVBox.getChildren().clear();

                        UserMemory.talkUser = userListView.getSelectionModel().getSelectedItem();

                        openChatInterface(image, getClass().getResource("fileImage/unknownFile.png"));
                    }
                };
            }
        });

        userName.setText(UserMemory.myUser.getUsername());
        Image image = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));
        userHead.setImage(image);
        buildUserList();
        primaryStage = ClientApp.getStage();
        minWindow.setOnAction(event -> primaryStage.setIconified(true));

        closeWindow.setOnAction((event) -> {
            primaryStage.close();
            System.exit(0);
        }); /* ???????????? */

        DragUtil.addDragListener(primaryStage, Collections.singletonList(userPane));


        poolExecutor.execute(() -> {
            try (ServerSocket serverSocket = new ServerSocket(clientPort)) {
                // ????????????????????????Interrupted?????????????????????????????? ?????????????????????
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    poolExecutor.execute(() -> {
                        log.info("????????????");
                        // ????????????
                        OutputStream os;//????????????????????????
                        try {
                            socket.setTcpNoDelay(true);
                            socket.setSoTimeout(5000);
                            os = socket.getOutputStream();
                            PrintStream ps = new PrintStream(os);
                            InputStream is = socket.getInputStream();//?????????????????????????????????
                            Reader reader = new InputStreamReader(is);//??????????????????????????????
                            BufferedReader bfr = new BufferedReader(reader);//????????????????????????????????????

                            JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                            // ?????????????????????
                            Integer code = Integer.valueOf(jsonObject.getString("code"));

                            if (Code.SEND_TEXT_MSG.equals(code)) {
                                TextMsg textMsg = JSON.parseObject(jsonObject.getString("object"), TextMsg.class);
                                UserMemory.textMsgList.add(textMsg);
                                Result result2 = new Result();
                                result2.setCode(Code.SEND_TEXT_MSG_SUCCESS);
                                ps.println(JSON.toJSONString(result2));
                                FileUtils.writeStringToFile(textMsgLog, JSON.toJSONString(textMsg) + "\n", StandardCharsets.UTF_8, true);

                                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                                Label userName = (Label) root.lookup("#userName");
                                Label sendTime = (Label) root.lookup("#sendTime");
                                Label sendContent = (Label) root.lookup("#sendContent");
                                ImageView senderImage = (ImageView) root.lookup("#senderImage");

                                userName.setText(UserMemory.talkUser.getUsername());
                                sendTime.setText(textMsg.getMessageTime());
                                senderImage.setImage(headImage);
                                sendContent.setText(textMsg.getContent());

                                Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));
                            } else if (Code.SEND_FILE_MSG.equals(code)) {
                                FileMsg fileMsg = JSON.parseObject(jsonObject.getString("object"), FileMsg.class);
                                UserMemory.users.forEach(user -> {
                                    if (user.getId().equals(fileMsg.getSenderId())) {
                                        UserMemory.talkUser = user;
                                    }
                                });
                                Platform.runLater(() -> openChatInterface(image, getClass().getResource("fileImage/unknownFile.png")));
                                receiveFileMsg(fileMsg, socket, getClass().getResource("fileImage/unknownFile.png"));
                                log.info("??????????????????");

                                FileUtils.writeStringToFile(fileMsgLog, JSON.toJSONString(fileMsg) + "\n", StandardCharsets.UTF_8, true);
                            } else if (Code.START_VIDEO_CHAT.equals(code)) {
                                String username = jsonObject.getString("object");
                                UserMemory.users.forEach(user -> {
                                    if (user.getUsername().equals(username)) {
                                        UserMemory.talkUser = user;
                                    }
                                });
                                Platform.runLater(() -> {
                                    JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
                                    VBox vBox = new VBox();
                                    vBox.setSpacing(20);
                                    Label label = new Label(username + "????????????????????????????????????");
                                    HBox hBox = new HBox();
                                    hBox.setSpacing(50);
                                    JFXButton start = new JFXButton("??????");
                                    JFXButton close = new JFXButton("??????");
                                    hBox.getChildren().addAll(start, close);
                                    vBox.getChildren().addAll(label, hBox);
                                    jfxDialogLayout.setBody(vBox);
                                    JFXAlert<Void> alert = new JFXAlert<>();
                                    alert.setOverlayClose(true);
                                    alert.setAnimation(JFXAlertAnimation.NO_ANIMATION);
                                    alert.setContent(jfxDialogLayout);
                                    alert.initModality(Modality.WINDOW_MODAL);
                                    alert.initStyle(StageStyle.TRANSPARENT);
                                    alert.setTitle("??????");
                                    start.setOnAction(event -> {
                                        Result result = new Result();
                                        result.setCode(Code.CONSENT_VIDEO_CHAT);
                                        ps.println(JSON.toJSONString(result));
                                        openChatInterface(image, getClass().getResource("fileImage/unknownFile.png"));
                                        chatInterface.talkVideoChat();
                                        alert.close();
                                    });
                                    close.setOnAction((event) -> {
                                        alert.close();
                                        Result result = new Result();
                                        result.setCode(Code.REFUSE_VIDEO_CHAT);
                                        ps.println(JSON.toJSONString(result));
                                    });
                                    alert.show();
                                });
                            } else if (Code.OFF_VIDEO_CHAT.equals(code)) {
                                chatInterface.videoAlert.close();
                                Result result = new Result();
                                ps.println(JSON.toJSONString(result));
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        });

    }

    private void openChatInterface(Image image, URL resource) {
        chatInterface.userName.setText(UserMemory.talkUser.getUsername());

        setUpMsgListView(image, resource);
        chatInterface.primaryStage.setTitle("???" + UserMemory.talkUser.getUsername() + "????????????");
        chatInterface.primaryStage.show();
    }

    private void receiveFileMsg(FileMsg fileMsg, Socket socket, URL resource) {
        File file = new File(System.getProperty("user.home") + "\\.socket\\" + "downloadFile\\" + fileMsg.getFileName());
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            try {
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (RandomAccessFile rw = new RandomAccessFile(file, "rw")) {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
            Label userName = (Label) root.lookup("#userName");
            Label sendTime = (Label) root.lookup("#sendTime");
            Label sendContent = (Label) root.lookup("#sendContent");
            ImageView senderImage = (ImageView) root.lookup("#senderImage");

            FileMsgVBox fileMsgVBox = new FileMsgVBox();

            sendContent.setGraphic(fileMsgVBox.fileMsgVBox(true, fileMsg.getFileName()));
            sendContent.setGraphicTextGap(0);

            fileMsgVBox.setFileImage(new Image(String.valueOf(resource)));
            fileMsgVBox.setHyperlink2OnAction(event2 -> {
                try {
                    Desktop.getDesktop().open(new File(System.getProperty("user.home") + "\\.socket\\downloadFile"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            User talkUser = null;
            for (User user : UserMemory.users) {
                if (user.getId().equals(fileMsg.getSenderId())) {
                    talkUser = user;
                }
            }

            userName.setText(talkUser != null ? talkUser.getUsername() : null);
            sendTime.setText(fileMsg.getMessageTime());
            senderImage.setImage(headImage);
            userName.setText(UserMemory.talkUser.getUsername());
            sendTime.setText(fileMsg.getMessageTime());
            senderImage.setImage(headImage);

            Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));

            rw.seek(fileMsg.getStartPoint());
            long length = fileMsg.getSize();
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] bytes = new byte[(int) (fileMsg.getSize() / 5 >= 1 ? fileMsg.getSize() / 5 : 1)];
            int len;
            long accumulationSize = fileMsg.getStartPoint();
            while ((len = dataInputStream.read(bytes)) != -1) {
                log.info("????????????" + len);
                rw.write(bytes, 0, len);
                accumulationSize += len;
                double progress = (double) accumulationSize / (double) length;
                Platform.runLater(() -> {
                    fileMsgVBox.setProgressBarProgress(progress);
                });
            }
            if (accumulationSize == length) {
                fileMsgVBox.setProgressBarState("????????????????????????");
            } else {
                fileMsgVBox.setProgressBarState("????????????????????????");
            }
            socket.close();
            fileMsg.setSign(1);
            fileMsg.setEndPoint(accumulationSize);
            fileMsg.setFileAddress(file.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void creatSendMsg(FileMsg fileMsg) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
            Label userName = (Label) root.lookup("#userName");
            Label sendTime = (Label) root.lookup("#sendTime");
            Label sendContent = (Label) root.lookup("#sendContent");
            ImageView senderImage = (ImageView) root.lookup("#senderImage");

            FileMsgVBox fileMsgVBox = new FileMsgVBox();
            sendContent.setGraphic(fileMsgVBox.fileMsgVBox(true, fileMsg.getFileName()));
            sendContent.setGraphicTextGap(0);
            sendContent.setStyle("-fx-background-color:  #95EC69; -fx-border-radius: 45; -fx-background-radius: 45;");

            fileMsgVBox.setFileImage(new Image(String.valueOf(new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\" + fileMsg.getFileName() + ".png").toURI())));
            fileMsgVBox.setProgressBarState("?????????????????????");

            userName.setText(UserMemory.talkUser.getUsername());
            sendTime.setText(fileMsg.getMessageTime());
            senderImage.setImage(headImage);

            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            Platform.runLater(() -> chatInterface.msgVBox.getChildren().add(root));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void groupChat(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/group.fxml")));

            FlowPane fPane = (FlowPane) root.lookup("#FPane");
            JFXCheckBox allGroup = (JFXCheckBox) root.lookup("#allGroup");
            JFXButton cancelGroup = (JFXButton) root.lookup("#cancelGroup");
            JFXButton sendGroup = (JFXButton) root.lookup("#sendGroup");
            JFXButton groupSendFileButton = (JFXButton) root.lookup("#groupSendFileButton");
            FontIcon fontIcon = new FontIcon(FontAwesome.FOLDER_O);
            fontIcon.setIconColor(Color.valueOf("#868A98FF"));
            fontIcon.setIconSize(18);
            groupSendFileButton.setGraphic(fontIcon);
            TextArea inputAreaGroup = (TextArea) root.lookup("#inputAreaGroup");

            sendGroup.setOnAction(event -> {
                String text = inputAreaGroup.getText();
                if (!"".equals(text)) {
                    for (User user : UserMemory.groupUser) {
                        Task<Void> task = new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                try {
                                    Date date = new Date();//??????????????????
                                    String msgTime = simpleDateFormat.format(date);//?????????????????????????????????????????????????????????????????????????????????????????????

                                    TextMsg textMsg = new TextMsg();
                                    Result result = new Result();
                                    textMsg.setMessageTime(msgTime);
                                    textMsg.setSenderId(UserMemory.myUser.getId());
                                    textMsg.setReceiverId(user.getId());
                                    textMsg.setContent(text);
                                    result.setObject(textMsg);
                                    if (1 == (user.getLogin())) {
                                        result.setCode(Code.SEND_TEXT_MSG);
                                        Result result2 = poolExecutor.submit(() -> {
                                            Result result3 = null;
                                            try {
                                                result3 = textMsgService.sendTextMsgByClient(result, user.getIp(), clientPort);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return result3;
                                        }).get();

                                        Platform.runLater(() -> {
                                            try {
                                                if (Code.SEND_TEXT_MSG_SUCCESS.equals(result2.getCode())) {
                                                    resetChatInterface(textMsg, inputAreaGroup);
                                                } else {
                                                    // ???????????? ??????????????????
                                                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                                    alert.show();
                                                    // ???????????????????????????
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });

                                    } else {
                                        result.setCode(Code.SEND_OFFLINE_TEXT_MSG);
                                        Result result2 = poolExecutor.submit(() -> {
                                            Result result3 = null;
                                            try {
                                                result3 = textMsgService.sendTextMsgByServer(result);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return result3;
                                        }).get();
                                        Platform.runLater(() -> {
                                            try {
                                                if (Code.SEND_OFFLINE_TEXT_MSG_SUCCESS.equals(result2.getCode())) {
                                                    resetChatInterface(textMsg, inputAreaGroup);
                                                } else {
                                                    // ???????????? ??????????????????
                                                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                                    alert.show();
                                                    // ???????????????????????????
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        });

                                    }

                                } catch (Exception e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "????????????");
                                    alert.show();
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        };
                        poolExecutor.submit(task);
                    }
                }
            });

            groupSendFileButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    File aimFile = fileChooser.showOpenDialog(primaryStage);
                    WritableImage fileIcon = GetFileIcon.getFileIcon(aimFile);

                    try {
                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/sendFile.fxml")));
                        Label fileNameLabel = (Label) root.lookup("#fileNameLabel");
                        ImageView fileImageView = (ImageView) root.lookup("#fileImageView");
                        JFXButton sendFileButton = (JFXButton) root.lookup("#sendFileButton");
                        JFXButton cancelButton = (JFXButton) root.lookup("#cancelButton");
                        ProgressBar sendProgressBar = (ProgressBar) root.lookup("#sendProgressBar");
                        Label progressLabel = (Label) root.lookup("#progressLabel");
                        Label sendStateLabel = (Label) root.lookup("#sendStateLabel");


                        fileNameLabel.setText(aimFile.getName());
                        fileImageView.setImage(fileIcon);
                        sendFileButton.setOnAction(event2 -> {
                            List<User> loginUser = new ArrayList<>();
                            List<User> noLoginUser = new ArrayList<>();
                            for (User user : UserMemory.groupUser) {
                                if (user.getLogin().equals(1)) {
                                    loginUser.add(user);
                                } else {
                                    noLoginUser.add(user);
                                }
                            }
                            List<Future<?>> futureList = new ArrayList<>();
                            for (User user : loginUser) {
                                Future<?> submit = poolExecutor.submit(() -> SendFile.sendFileMsg(aimFile, simpleDateFormat, poolExecutor, fileMsgService, clientPort, user));
                                futureList.add(submit);
                            }
                            Future<?> submit = poolExecutor.submit(() -> SendFile.sendFileMsg(aimFile, simpleDateFormat, poolExecutor, fileMsgService, noLoginUser));
                            futureList.add(submit);
                            Platform.runLater(() -> {
                                int length = futureList.size();
                                for (int i = 0; i < futureList.size(); i++) {
                                    try {
                                        futureList.get(i).get();
                                    } catch (Exception e) {
                                        log.error(e.toString());
                                    }
                                    progressLabel.setText((double) (i + 1) / (double) length * 100 + "%");
                                    sendProgressBar.setProgress((double) (i + 1) / (double) length);
                                }
                            });
                        });

                        JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
                        jfxDialogLayout.setBody(root);
                        JFXAlert<Void> alert = new JFXAlert<>();
                        alert.setOverlayClose(true);
                        alert.setAnimation(JFXAlertAnimation.NO_ANIMATION);
                        alert.setTitle("????????????");
                        alert.setContent(jfxDialogLayout);
                        alert.initModality(Modality.WINDOW_MODAL);
                        cancelButton.setOnAction(event2 -> alert.close());
                        alert.showAndWait();

                    } catch (IOException e) {
                        log.error(e.toString());
                    }

                }
            });

            UserMemory.groupUser = new ArrayList<>();
            List<JFXCheckBox> jfxCheckBoxes = new ArrayList<>();
            UserMemory.users.forEach(user -> {
                JFXCheckBox jfxCheckBox = new JFXCheckBox(user.getUsername());
                jfxCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        UserMemory.groupUser.add(user);
                    } else {
                        UserMemory.groupUser.remove(user);
                    }
                });
                jfxCheckBoxes.add(jfxCheckBox);
                fPane.getChildren().add(jfxCheckBox);
            });

            allGroup.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    jfxCheckBoxes.forEach(jfxCheckBox -> jfxCheckBox.setSelected(true));
                } else {
                    jfxCheckBoxes.forEach(jfxCheckBox -> jfxCheckBox.setSelected(false));
                }
            });

            JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
            jfxDialogLayout.setBody(root);
            JFXAlert<Void> alert = new JFXAlert<>();
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.NO_ANIMATION);
            alert.setContent(jfxDialogLayout);
            alert.initModality(Modality.NONE);
            cancelGroup.setOnAction(event -> alert.close());
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetChatInterface(TextMsg textMsg, TextArea inputAreaGroup) {
        inputAreaGroup.setText("");
        UserMemory.textMsgList.add(textMsg);
    }
}
