package com.client.controller;

import com.client.config.ProgressStageConfig;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.TextMsg;
import com.client.service.FileMsgService;
import com.client.service.TextMsgService;
import com.client.service.UserService;
import com.client.utils.GetFileIcon;
import com.client.utils.SendFile;
import com.client.utils.UserMemory;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author Administrator
 */
@FXMLController
@Slf4j
public class ChatInterfaceController implements Initializable {
    @FXML
    public TextArea inputArea;
    @FXML
    public Label userName;
    public Stage primaryStage;
    @FXML
    public AnchorPane headPane;

    @FXML
    public VBox msgVBox;
    public ScrollPane msgScrollPane;
    @FXML
    public JFXButton videoChatButton;
    @FXML
    private JFXButton fileChoiceButton;

    @Value("${client.port}")
    private Integer clientPort;

    @Autowired
    private FileMsgService fileMsgService;
    @Autowired
    private TextMsgService textMsgService;
    @Autowired
    private ThreadPoolExecutor poolExecutor;
    @Autowired
    private ProgressStageConfig progressStageConfig;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

    @Value("${client.udp.video.port}")
    private Integer clientUdpVideoPort;

    @Value("${client.udp.audio.port}")
    private Integer clientUdpAudioPort;

    @Autowired
    private UserService userService;

    @FXML
    private Button minWindow;
    @FXML
    private Button closeWindow;
    @FXML
    private Button sendButton;

    private FileChooser fileChooser;

    private Image headImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        headImage = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));
        // 最小化
        minWindow.setOnAction(event -> primaryStage.setIconified(true));
        closeWindow.setOnAction(event -> primaryStage.close());
        FontIcon fontIcon = new FontIcon(FontAwesome.FOLDER_O);
        fontIcon.setIconColor(Color.valueOf("#868A98FF"));
        fontIcon.setIconSize(18);
        fileChoiceButton.setGraphic(fontIcon);
        fileChooser = new FileChooser();
        msgScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        msgScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        msgScrollPane.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> msgScrollPane.setVvalue(1));
        FontIcon fontIcon2 = new FontIcon(FontAwesome.PHONE_SQUARE);
        fontIcon2.setIconColor(Color.valueOf("#00FFE7FF"));
        fontIcon2.setIconSize(18);
        videoChatButton.setGraphic(fontIcon2);
    }

    public void sendTextMsg(ActionEvent mouseEvent) {
        String text = inputArea.getText();
        if (!"".equals(text)) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        Date date = new Date();//获得当前时间
                        String msgTime = simpleDateFormat.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

                        TextMsg textMsg = new TextMsg();
                        Result result = new Result();
                        textMsg.setMessageTime(msgTime);
                        textMsg.setSenderId(UserMemory.myUser.getId());
                        textMsg.setReceiverId(UserMemory.talkUser.getId());
                        textMsg.setContent(text);
                        result.setObject(textMsg);

                        if (1 == (UserMemory.talkUser.getLogin())) {
                            result.setCode(Code.SEND_TEXT_MSG);
                            Result result2 = poolExecutor.submit(() -> {
                                Result result3 = null;
                                try {
                                    result3 = textMsgService.sendTextMsgByClient(result, UserMemory.talkUser.getIp(), clientPort);
                                } catch (Exception e) {
                                    log.error(e.toString());
                                    e.printStackTrace();
                                }
                                return result3;
                            }).get();

                            Platform.runLater(() -> {
                                try {
                                    if (Code.SEND_TEXT_MSG_SUCCESS.equals(result2.getCode())) {
                                        resetChatInterface(textMsg);
                                        Image image = new Image(String.valueOf(getClass().getResource("headImage/head.gif")));
                                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                                        Label userName = (Label) root.lookup("#userName");
                                        Label sendTime = (Label) root.lookup("#sendTime");
                                        Label sendContent = (Label) root.lookup("#sendContent");
                                        ImageView senderImage = (ImageView) root.lookup("#senderImage");

                                        userName.setText(UserMemory.myUser.getUsername());
                                        sendTime.setText(textMsg.getMessageTime());
                                        senderImage.setImage(image);
                                        sendContent.setText(textMsg.getContent());

                                        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                                        msgVBox.getChildren().add(root);
                                    } else {
                                        // 发送失败 弹出错误窗口
                                        Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                        alert.show();
                                        // 失败的话得重新输入
                                    }
                                } catch (Exception e) {
                                    log.error(e.toString());
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
                                    log.error(e.toString());
                                    e.printStackTrace();
                                }
                                return result3;
                            }).get();

                            Platform.runLater(() -> {
                                try {
                                    if (Code.SEND_OFFLINE_TEXT_MSG_SUCCESS.equals(result2.getCode())) {
                                        resetChatInterface(textMsg);
                                        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/msgCell.fxml")));
                                        Label userName = (Label) root.lookup("#userName");
                                        Label sendTime = (Label) root.lookup("#sendTime");
                                        Label sendContent = (Label) root.lookup("#sendContent");
                                        ImageView senderImage = (ImageView) root.lookup("#senderImage");

                                        sendContent.setStyle("-fx-background-color:  #95EC69; -fx-border-radius: 45; -fx-background-radius: 45;");
                                        userName.setText(UserMemory.myUser.getUsername());
                                        sendTime.setText(textMsg.getMessageTime());
                                        senderImage.setImage(headImage);
                                        sendContent.setText(textMsg.getContent());

                                        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                                        msgVBox.getChildren().add(root);
                                    } else {
                                        // 发送失败 弹出错误窗口
                                        Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                        alert.show();
                                        // 失败的话得重新输入
                                    }
                                } catch (Exception e) {
                                    log.error(e.toString());
                                    e.printStackTrace();
                                }
                            });

                        }
                    } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "未知错误");
                        alert.show();
                        log.error(e.toString());
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            poolExecutor.submit(task);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "发送的消息不能为空！");
            alert.show();
        }


    }

    private void resetChatInterface(TextMsg textMsg) {
        inputArea.setText("");
        UserMemory.textMsgList.add(textMsg);
    }

    public void choiceFileEvent(ActionEvent actionEvent) {
        fileChooser.setTitle("请选择你想要的文件");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All File", "*.*")
        );

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
            sendFileButton.setOnAction(event -> {
                if (UserMemory.talkUser.getLogin().equals(1)) {
                    poolExecutor.execute(() -> SendFile.sendFileMsg(aimFile, simpleDateFormat, poolExecutor, fileMsgService, clientPort, sendProgressBar, progressLabel, sendStateLabel, headImage, msgVBox));
                } else {
                    poolExecutor.execute(() -> SendFile.sendFileMsg(aimFile, simpleDateFormat, poolExecutor, fileMsgService, sendProgressBar, progressLabel, sendStateLabel, headImage, msgVBox));
                }
            });

            JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
            jfxDialogLayout.setBody(root);
            JFXAlert<Void> alert = new JFXAlert<>(primaryStage);
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
            alert.setTitle("发送文件");
            alert.setContent(jfxDialogLayout);
            alert.initModality(Modality.WINDOW_MODAL);
            cancelButton.setOnAction((event) -> alert.close());
            alert.showAndWait();

        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void startVideoChat(ActionEvent event) {
        try {
            if (UserMemory.talkUser.getLogin().equals(1)) {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/videoChat.fxml")));

                AnchorPane talkVideoPane = (AnchorPane) root.lookup("#talkVideoPane");
                Label talkUsernameLabel = (Label) root.lookup("#talkUsernameLabel");
                Label myUsernameLabel = (Label) root.lookup("#myUsernameLabel");
                AnchorPane myVideoPane = (AnchorPane) root.lookup("#myVideoPane");
                JFXButton hangUpButton = (JFXButton) root.lookup("#hangUpButton");
                ImageView imageView = new ImageView();
                talkVideoPane.getChildren().add(imageView);

                FontIcon fontIcon = new FontIcon(FontAwesome.PHONE);
                fontIcon.setIconColor(Color.RED);
                fontIcon.setIconSize(30);
                hangUpButton.setGraphic(fontIcon);

                Webcam webcam = Webcam.getDefault();
                webcam.setViewSize(WebcamResolution.VGA.getSize());

                WebcamPanel panel = new WebcamPanel(webcam);
                panel.setFPSDisplayed(true);
                panel.setDisplayDebugInfo(true);
                panel.setImageSizeDisplayed(true);
                panel.setMirrored(true);

                SwingNode swingNode = new SwingNode();
                myUsernameLabel.setText(UserMemory.myUser.getUsername());
                talkUsernameLabel.setText(UserMemory.talkUser.getUsername());
                swingNode.setContent(panel);
                myVideoPane.getChildren().add(swingNode);

                JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
                jfxDialogLayout.setBody(root);
                JFXAlert<Void> alert = new JFXAlert<>();
                alert.setOverlayClose(true);
                alert.setAnimation(JFXAlertAnimation.NO_ANIMATION);
                alert.setSize(1280, 680);
                alert.setTitle("视频通话");
                alert.setContent(jfxDialogLayout);
                alert.initModality(Modality.NONE);

                Thread sendVideoThread = new Thread(() -> {
                    try (DatagramSocket datagramSocket = new DatagramSocket()) {
                        log.info(UserMemory.myUser.getUsername() + "发送视频");
                        sendVideo(webcam, datagramSocket);
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                });

                Thread sendAudioThread = new Thread(() -> {
                    try (DatagramSocket datagramSocket = new DatagramSocket()) {
                        log.info(UserMemory.myUser.getUsername() + "发送音频");
                        sendAudio(datagramSocket);
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                });

                Thread receiveVideoThread = new Thread(() -> {
                    try (DatagramSocket datagramSocket = new DatagramSocket(clientUdpVideoPort)) {
                        log.info(UserMemory.myUser.getUsername() + "接收到视频");
                        receiveVideo(datagramSocket, imageView);
                    } catch (IOException e) {
                        log.error(e.toString());
                    }
                });

                Thread receiveAudioThread = new Thread(() -> {
                    try (DatagramSocket datagramSocket = new DatagramSocket(clientUdpAudioPort)) {
                        log.info(UserMemory.myUser.getUsername() + "接收到音频");
                        receiveAudio(datagramSocket);
                    } catch (Exception e) {
                        log.error(e.toString());
                    }
                });

                poolExecutor.execute(() -> {
                    Result result = new Result();
                    result.setCode(Code.START_VIDEO_CHAT);
                    result.setObject(UserMemory.myUser.getUsername());
                    Result result2 = userService.videoChat(result, UserMemory.talkUser.getIp(), clientPort);
                    if (Code.CONSENT_VIDEO_CHAT.equals(result2.getCode())) {
                        sendVideoThread.start();
//                        sendAudioThread.start();
                        receiveVideoThread.start();
//                        receiveAudioThread.start();
                    } else {
                        Platform.runLater(() -> {
                            Alert alert1 = new Alert(Alert.AlertType.ERROR, "对方拒绝和你视频");
                            alert1.setOnCloseRequest(event2 -> alert.close());
                        });
                    }
                });

                alert.setOnCloseRequest(event1 -> {
                    webcam.close();
                    sendVideoThread.interrupt();
                    sendAudioThread.interrupt();
                    receiveAudioThread.interrupt();
                    receiveVideoThread.interrupt();
                });

                hangUpButton.setOnAction(event2 -> alert.close());

                alert.showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "对方不在线").showAndWait();
            }
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public void talkVideoChat() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/videoChat.fxml")));

            AnchorPane talkVideoPane = (AnchorPane) root.lookup("#talkVideoPane");
            Label talkUsernameLabel = (Label) root.lookup("#talkUsernameLabel");
            Label myUsernameLabel = (Label) root.lookup("#myUsernameLabel");
            AnchorPane myVideoPane = (AnchorPane) root.lookup("#myVideoPane");
            JFXButton hangUpButton = (JFXButton) root.lookup("#hangUpButton");
            ImageView imageView = new ImageView();
            talkVideoPane.getChildren().add(imageView);

            FontIcon fontIcon = new FontIcon(FontAwesome.PHONE);
            fontIcon.setIconColor(Color.RED);
            fontIcon.setIconSize(30);
            hangUpButton.setGraphic(fontIcon);

            Webcam webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());

            WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setDisplayDebugInfo(true);
            panel.setImageSizeDisplayed(true);
            panel.setMirrored(true);

            SwingNode swingNode = new SwingNode();
            myUsernameLabel.setText(UserMemory.myUser.getUsername());
            talkUsernameLabel.setText(UserMemory.talkUser.getUsername());
            swingNode.setContent(panel);
            myVideoPane.getChildren().add(swingNode);

            JFXDialogLayout jfxDialogLayout = new JFXDialogLayout();
            jfxDialogLayout.setBody(root);
            JFXAlert<Void> alert = new JFXAlert<>();
            alert.setOverlayClose(true);
            alert.setAnimation(JFXAlertAnimation.NO_ANIMATION);
            alert.setSize(1280, 680);
            alert.setTitle("视频通话");
            alert.setContent(jfxDialogLayout);
            alert.initModality(Modality.NONE);

            Thread sendVideoThread = new Thread(() -> {
                log.info(UserMemory.myUser.getUsername() + "发送视频");
                try (DatagramSocket datagramSocket = new DatagramSocket()) {
                    sendVideo(webcam, datagramSocket);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            });

            Thread sendAudioThread = new Thread(() -> {
                log.info(UserMemory.myUser.getUsername() + "发送音频");
                try (DatagramSocket datagramSocket = new DatagramSocket()) {
                    sendAudio(datagramSocket);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            });

            sendVideoThread.start();
//            sendAudioThread.start();

            Thread receiveVideoThread = new Thread(() -> {
                try (DatagramSocket datagramSocket = new DatagramSocket(clientUdpVideoPort)) {
                    log.info(UserMemory.myUser.getUsername() + "接收到视频");
                    receiveVideo(datagramSocket, imageView);
                } catch (IOException e) {
                    log.error(e.toString());
                }
            });

            Thread receiveAudioThread = new Thread(() -> {
                try (DatagramSocket datagramSocket = new DatagramSocket(clientUdpAudioPort)) {
                    log.info(UserMemory.myUser.getUsername() + "接收到音频");
                    receiveAudio(datagramSocket);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            });

            receiveVideoThread.start();
//            receiveAudioThread.start();

            alert.setOnCloseRequest(event1 -> {
                webcam.close();
                sendVideoThread.interrupt();
                sendAudioThread.interrupt();
                receiveVideoThread.interrupt();
                receiveAudioThread.interrupt();
            });

            hangUpButton.setOnAction(event2 -> alert.close());

            alert.showAndWait();
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    private void sendVideo(Webcam webcam, DatagramSocket datagramSocket) throws IOException {
        while (Thread.currentThread().isInterrupted()) {
            BufferedImage image = webcam.getImage();
            if (image != null) {
                BufferedImage bufferedImage = Thumbnails.of(image).scale(0.5).asBufferedImage();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "JPEG", byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(UserMemory.talkUser.getIp()), clientUdpVideoPort);
                datagramSocket.send(packet);
            }
        }
    }

    private void receiveAudio(DatagramSocket datagramSocket) throws LineUnavailableException, IOException {
        datagramSocket.setSoTimeout(5000);
        AudioFormat format = new AudioFormat(48000, 24, 1, true, false);
        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
        sourceDataLine.open(format);
        sourceDataLine.start();
        byte[] bytes = new byte[1024];
        while (Thread.currentThread().isInterrupted()) {
            DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length);
            datagramSocket.receive(packet);
            if (packet.getLength() != -1) {
                sourceDataLine.write(bytes, 0, bytes.length);
                log.info("收到了音频");
            }
        }
    }

    private void receiveVideo(DatagramSocket datagramSocket, ImageView imageView) throws IOException {
        datagramSocket.setSoTimeout(5000);
        WritableImage writableImage = new WritableImage(640, 480);
        while (Thread.currentThread().isInterrupted()) {
            byte[] bytes = new byte[1024 * 64];
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
            datagramSocket.receive(packet);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes, 0, packet.getLength()));
            log.info(image.toString());
            SwingFXUtils.toFXImage(image, writableImage);
            Platform.runLater(() -> imageView.setImage(writableImage));
            log.info("收到了图片");
        }
    }

    private void sendAudio(DatagramSocket datagramSocket) throws LineUnavailableException, IOException {
        AudioFormat format = new AudioFormat(48000, 24, 1, true, false);
        TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format);
        targetDataLine.open(format);
        targetDataLine.start();
        byte[] bytes = new byte[1024];
        while (Thread.currentThread().isInterrupted()) {
            int read = targetDataLine.read(bytes, 0, bytes.length);
            if (read != -1) {
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(UserMemory.talkUser.getIp()), clientUdpAudioPort);
                datagramSocket.send(packet);
            }
        }
    }
}