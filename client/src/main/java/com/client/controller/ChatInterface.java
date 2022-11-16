package com.client.controller;

import com.alibaba.fastjson.JSON;
import com.client.pojo.*;
import com.client.service.FileMsgService;
import com.client.service.TextMsgService;
import com.client.service.UserService;
import com.client.utils.GetFileIcon;
import com.client.utils.ProgressStageUtil;
import com.client.utils.UserMemory;
import com.jfoenix.controls.JFXButton;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;


@FXMLController
@Slf4j
public class ChatInterface implements Initializable {

    @FXML
    public TextArea inputArea;
    @FXML
    public Label userName;
    public Stage primaryStage;

    @FXML
    public AnchorPane headPane;

    @FXML
    private JFXButton fileChoiceButton;

    @Autowired
    private FileMsgService fileMsgService;
    @Autowired
    private TextMsgService textMsgService;
    @Autowired
    private ThreadPoolExecutor poolExecutor;

    @Autowired
    private UserService userService;

    @FXML
    private Button minWindow;
    @FXML
    private Button closeWindow;
    @FXML
    private Button sendButton;

    private FileChooser fileChooser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction(event -> {
            primaryStage.close();
        });

        FontIcon fontIcon = new FontIcon(FontAwesome.FOLDER_O);
        fontIcon.setIconColor(Color.valueOf("#868A98FF"));
        fontIcon.setIconSize(18);
        fileChoiceButton.setGraphic(fontIcon);
        fileChooser = new FileChooser();
    }

    public void sendTextMsg(ActionEvent mouseEvent) {
        String text = inputArea.getText();
        if (!text.equals("")) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月和小时的格式为两个大写字母
                        Date date = new Date();//获得当前时间
                        String msgTime = df.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

                        TextMsg textMsg = new TextMsg();
                        Result result = new Result();
                        textMsg.setMessageTime(msgTime);
                        textMsg.setSenderId(UserMemory.myUser.getId());
                        textMsg.setReceiveId(UserMemory.talkUser.getId());
                        result.setObject(textMsg);

                        if (1 == (UserMemory.talkUser.getLogin())) {
                            result.setCode(Code.SEND_TEXT_MSG);
                        } else {
                            result.setCode(Code.SEND_OFFLINE_TEXT_MSG);
                        }

                        textMsg.setContent(text);
                        Result result2 = poolExecutor.submit(() -> {
                            Result result3 = null;
                            try {
                                result3 = textMsgService.sendTextMsgByServer(result);
                                UserMemory.users = JSON.parseArray(result3.getObject().toString(), User.class);
                            } catch (Exception e) {
                                log.error(e.toString());
                            }
                            return result3;
                        }).get();

                        Platform.runLater(() -> {
                            try {
                                if (Code.SEND_OFFLINE_TEXT_MSG_SUCCESS.equals(result2.getCode())) {
                                    // 发送成功弹窗 显示服务器返回的信息
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION, result2.getMsg());
                                    alert.showAndWait();
                                } else {
                                    // 发送失败 弹出错误窗口
                                    Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                    alert.show();
                                    // 失败的话得重新输入
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
            ProgressStageUtil.of(primaryStage, poolExecutor, task, "发送中");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "发送的消息不能为空！");
            alert.show();
        }


    }

    public void choiceFileEvent(ActionEvent actionEvent) {
        fileChooser.setTitle("请选择你想要的文件");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All File","*.*")
        );

        File aimFile = fileChooser.showOpenDialog(primaryStage);

        // 获取文件图标
        WritableImage fileIcon = GetFileIcon.getFileIcon(aimFile);

        if (aimFile.isFile()) {
            poolExecutor.execute(() -> {
                try {
                    FileMsg fileMsg = new FileMsg();
                    Result result = new Result();
                    if (1 == (UserMemory.talkUser.getLogin())) {
                        result.setCode(Code.SEND_FILE_MSG);
                    } else {
                        result.setCode(Code.SEND_OFFLINE_FILE_MSG);
                    }

                    Socket socket = poolExecutor.submit(() -> {
                        Socket socket2 = null;
                        try {
                            socket2 = fileMsgService.sendOfflineFileMsg();
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                        return socket2;
                    }).get();

                    Result result2 = poolExecutor.submit(() -> {
                        // 一些流的封装
                        OutputStream os = socket.getOutputStream();
                        InputStream is = socket.getInputStream();

                        // 读写文本消息的流
                        PrintStream socketPrintStream = new PrintStream(os);
                        BufferedReader socketReader = new BufferedReader(new InputStreamReader(is));

                        // 发送文件流，字节缓冲流即可
                        BufferedOutputStream socketOutputStream = new BufferedOutputStream(socket.getOutputStream());

                        // 同意发送即开始发送

                        File logFile = new File(System.getProperty("user.home") + "\\.socket\\" + aimFile.getName() + ".log");
                        try (
                                // 操作日志文件
                                BufferedReader logFileReader = new BufferedReader(new FileReader(logFile));
                                PrintWriter logFileWriter = new PrintWriter(logFile);
                                // 读目标文件
                                RandomAccessFile randomAccessAimFile = new RandomAccessFile(aimFile, "r")) {
                            // 文件操作的流(单线程模式传文件)
                            // 1.创建或读取日志文件记录发送点位
                            if (!logFile.exists()) {
                                logFile.createNewFile();
                            }
                            // 读取上次传送的位置
                            long pos = Long.parseLong(logFileReader.readLine());
                            // 设置读取的起始位置
                            randomAccessAimFile.seek(pos);
                            // 开始传输文件
                            byte[] bytes = new byte[1024 * 10];
                            int len = 0;
                            Long accumulationSize = 0L;
                            while ((len = randomAccessAimFile.read(bytes)) != -1) {
                                socketOutputStream.write(bytes, 0, len);
                                accumulationSize += len;
                                logFileWriter.println(accumulationSize);
                                // 暂停 停止传输
                            }
                            Result result21 = new Result();
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月和小时的格式为两个大写字母
                            Date date = new Date();//获得当前时间
                            String msgTime = df.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

                            // 和服务器 发送 完成操作码
                            fileMsg.setMessageTime(msgTime);
                            fileMsg.setSenderId(UserMemory.myUser.getId());
                            fileMsg.setReceiveId(UserMemory.talkUser.getId());
                            fileMsg.setPos(accumulationSize + pos);
                            result21.setCode(Code.SEND_COMPLETED_OFFLINE_FILE_MSG);
                            result21.setObject(fileMsg);

                            socketPrintStream.println(result21);

                            return JSON.parseObject(socketReader.readLine(), Result.class);
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                        return null;
                    }).get();

                    Platform.runLater(() -> {
                        try {
                            if (Code.SEND_OFFLINE_FILE_MSG_SUCCESS.equals(result2.getCode())) {
                                // 发送成功弹窗 显示服务器返回的信息
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, result2.getMsg());
                                alert.showAndWait();
                            } else {
                                // 发送失败 弹出错误窗口
                                Alert alert = new Alert(Alert.AlertType.ERROR, result2.getMsg());
                                alert.show();
                                // 失败的话得重新输入
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
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "非文件！");
            alert.show();
        }
    }
}