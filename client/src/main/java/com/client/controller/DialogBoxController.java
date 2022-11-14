package com.client.controller;

import com.client.ClientApp;
import com.client.view.UserView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


@FXMLController
public class DialogBoxController implements Initializable {

    @FXML
    public Label userName;
    public Stage primaryStage;
    @FXML
    private Button minWindow;
    @FXML
    private Button closeWindow;
    @FXML
    private Button sendButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        minWindow.setOnAction(event -> primaryStage.setIconified(true)); /* 最小化 */
        closeWindow.setOnAction(event -> {
            primaryStage.close();
        });
    }

    public void sendMessage(ActionEvent mouseEvent) {
    }

    public void returnToUser(ActionEvent event) {
        ClientApp.showView(UserView.class);
    }  //点击关闭后返回列表界面

}