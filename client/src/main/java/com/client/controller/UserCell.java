package com.client.controller;

import com.client.pojo.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class UserCell extends BaseCell<User> {

    @FXML
    private ImageView headImage;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label msgLabel;
    @FXML
    private Label dataLabel;


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
}
