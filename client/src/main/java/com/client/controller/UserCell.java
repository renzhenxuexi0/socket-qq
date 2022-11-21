package com.client.controller;

import com.client.pojo.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class UserCell extends BaseCell<User> implements Initializable {

    @FXML
    public AnchorPane mainPane;

    @FXML
    public Circle stateCircle;
    @FXML
    private Label usernameLabel;

    /**
     * 构造表上某一列的视图
     * 自定义控件的FXML资源定位符，如果为空，则简单展示文本
     */
    public UserCell() {
        super(UserCell.class.getResource("fxml/userCell.fxml"));
    }

    @Override
    public void bindData(User item) {
        usernameLabel.setText(item.getUsername());
        if (item.getLogin().equals(1)) {
            stateCircle.setFill(Color.rgb(72, 255, 0));
        } else {
            stateCircle.setFill(Color.valueOf("#cdcdcd"));
        }
    }

    public abstract EventHandler<? super MouseEvent> setOnclickBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setOnMouseClicked(setOnclickBox());

    }
}
