package com.client.controller;

import com.client.pojo.Msg;
import com.client.pojo.User;
import com.client.utils.UserMemory;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class MsgCell<T> extends BaseCell<User> implements Initializable {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private ImageView senderImage;

    @FXML
    private Label userName;

    @FXML
    private Label sendTime;

    @FXML
    private Label sendText;
    /**
     * 构造表上某一列的视图
     * 自定义控件的FXML资源定位符，如果为空，则简单展示文本
     */
    public MsgCell() {
        super(MsgCell.class.getResource("fxml/msgCell.fxml"));
    }

    public void bindData(Msg<T> item) {
        userName.setText(item.getUsername());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }


}
