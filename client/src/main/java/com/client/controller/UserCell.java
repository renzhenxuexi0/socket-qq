package com.client.controller;

import com.client.pojo.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public abstract class UserCell extends BaseCell<User> implements Initializable {

    @FXML
    public AnchorPane mainPane;

    public Label dateLabel;
    @FXML
    private ImageView headImage;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label msgLabel;

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
    }

    public abstract EventHandler<? super MouseEvent> setOnclickBox();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setOnMouseClicked(setOnclickBox());

    }
}
