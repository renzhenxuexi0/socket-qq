package com.client.controller;

import com.client.pojo.FileMsg;
import com.client.pojo.SendMsg;
import com.client.pojo.TextMsg;
import com.client.utils.UserMemory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

@Slf4j
public class MsgCell extends BaseCell<SendMsg> implements Initializable {
    @FXML
    private AnchorPane mainPane;

    private SimpleDateFormat simpleDateFormat;

    @FXML
    private ImageView senderImage;

    @FXML
    private Label userName;

    @FXML
    private Label sendTime;

    @FXML
    private Label sendContent;

    /**
     * 构造表上某一列的视图
     * 自定义控件的FXML资源定位符，如果为空，则简单展示文本
     */
    public MsgCell() {
        super(MsgCell.class.getResource("fxml/msgCell.fxml"));
    }

    public void bindData(SendMsg item) {
        if (item.getType() == 0) {
            TextMsg msg = (TextMsg) item.getMsg();
            sendContent.setText(msg.getContent());
            sendTime.setText(msg.getMessageTime());
            if (msg.getSenderId().equals(UserMemory.myUser.getId())) {
                mainPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                userName.setText(UserMemory.myUser.getUsername());
            } else {
                userName.setText(UserMemory.talkUser.getUsername());
            }
        } else {
            FileMsg msg = (FileMsg) item.getMsg();
            sendTime.setText(msg.getMessageTime());
            if (msg.getSenderId().equals(UserMemory.myUser.getId())) {
                mainPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                userName.setText(UserMemory.myUser.getUsername());
            } else {
                userName.setText(UserMemory.talkUser.getUsername());
            }

            sendContent.setGraphic(item.getVBox());
            sendContent.setGraphicTextGap(0);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
