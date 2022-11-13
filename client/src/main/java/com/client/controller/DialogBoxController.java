package com.client.controller;

import com.client.ClientApp;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


@FXMLController
public class DialogBoxController implements Initializable {

    private Stage stage;

    Scene scene;

    @FXML
    private TextArea input;

    private Stage primaryStage;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button close;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void sendMessage(ActionEvent mouseEvent) {

    }

    public void returnToUser(ActionEvent event) {
        ClientApp.showView(UserView.class);
    }  //点击关闭后返回列表界面
}