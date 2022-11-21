package com.client.controller;

import com.client.config.ProgressStageConfig;
import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.pojo.TextMsg;
import com.client.service.TextMsgService;
import com.client.utils.UserMemory;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import de.felixroske.jfxsupport.FXMLController;
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
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class GroupChatController {


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




}
