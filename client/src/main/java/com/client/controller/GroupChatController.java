package com.client.controller;

import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.TextMsg;
import com.client.service.TextMsgService;
import com.client.utils.UserMemory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class GroupChatController {

    @Autowired
    ThreadPoolExecutor poolExecutor;
    @Autowired
    SimpleDateFormat simpleDateFormat;
    @Autowired
    TextMsgService textMsgService;
    @FXML
    private GridPane GPane;
    @FXML
    private JFXCheckBox allGroup;
    @FXML
    private JFXButton sendGroup;
    @FXML
    private JFXButton cancelGroup;
    @FXML
    private FlowPane FPane;
    @FXML
    private JFXButton fileChoiceButtonGroup;
    @FXML
    private JFXTextArea inputAreaGroup;

    @FXML
    void choiceFileEvent(ActionEvent event) {

    }

    @FXML
    void sendTextMsg(ActionEvent event) {
        String text = inputAreaGroup.getText();
        if (!"".equals(text)) {
            Task<Void> task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    UserMemory.groupUser.forEach(user -> {
                        try {
                            Date date = new Date();//获得当前时间
                            String msgTime = simpleDateFormat.format(date);//将当前时间转换成特定格式的时间字符串，这样便可以插入到数据库中

                            TextMsg textMsg = new TextMsg();
                            Result result = new Result();
                            textMsg.setMessageTime(msgTime);
                            textMsg.setSenderId(UserMemory.myUser.getId());
                            textMsg.setReceiverId(user.getId());
                            textMsg.setContent(text);
                            result.setObject(textMsg);
                            if (1 == (UserMemory.talkUser.getLogin())) {
                                result.setCode(Code.SEND_TEXT_MSG);
                                Result result2 = poolExecutor.submit(() -> {
                                    Result result3 = null;
                                    try {
                                        result3 = textMsgService.sendTextMsgByClient(result, user.getIp(), 8081);
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
                    });
                    return null;
                }
            };
        }
    }

    private void resetChatInterface(TextMsg textMsg) {
        inputAreaGroup.setText("");
        UserMemory.textMsgList.add(textMsg);
    }

}
