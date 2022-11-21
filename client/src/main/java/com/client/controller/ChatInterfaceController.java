package com.client.controller;

import com.client.config.ProgressStageConfig;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.SendMsg;
import com.client.pojo.TextMsg;
import com.client.service.FileMsgService;
import com.client.service.TextMsgService;
import com.client.service.UserService;
import com.client.utils.MsgMemory;
import com.client.utils.SendFile;
import com.client.utils.UserMemory;
import com.jfoenix.controls.JFXButton;
import de.felixroske.jfxsupport.FXMLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public ListView<SendMsg> msgListView;
    @FXML
    private JFXButton fileChoiceButton;

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
        // 最小化
        minWindow.setOnAction(event -> primaryStage.setIconified(true));
        closeWindow.setOnAction(event -> primaryStage.close());
        FontIcon fontIcon = new FontIcon(FontAwesome.FOLDER_O);
        fontIcon.setIconColor(Color.valueOf("#868A98FF"));
        fontIcon.setIconSize(18);
        fileChoiceButton.setGraphic(fontIcon);
        fileChooser = new FileChooser();
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
                                    result3 = textMsgService.sendTextMsgByClient(result, UserMemory.talkUser.getIp(), 8081);
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

            progressStageConfig.setParent(primaryStage);
            progressStageConfig.setText("发送中");
            progressStageConfig.setWork(task);
            progressStageConfig.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "发送的消息不能为空！");
            alert.show();
        }


    }

    private void resetChatInterface(TextMsg textMsg) {
        inputArea.setText("");
        UserMemory.textMsgList.add(textMsg);
        SendMsg sendMsg = new SendMsg();
        sendMsg.setMsg(textMsg);
        sendMsg.setType(0);
        MsgMemory.sendMsgList.add(sendMsg);
        MsgMemory.sendMsgListSort(simpleDateFormat);
        msgListView.setItems(FXCollections.observableArrayList(MsgMemory.sendMsgList));
    }

    public void choiceFileEvent(ActionEvent actionEvent) {
        fileChooser.setTitle("请选择你想要的文件");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All File", "*.*")
        );

        File aimFile = fileChooser.showOpenDialog(primaryStage);

        poolExecutor.execute(() -> SendFile.sendFileMsg(aimFile, simpleDateFormat, poolExecutor, fileMsgService, msgListView));
    }
}