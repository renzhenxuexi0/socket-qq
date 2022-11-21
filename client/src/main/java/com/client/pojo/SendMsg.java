package com.client.pojo;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import lombok.Data;

@Data
public class SendMsg {
    private Msg msg;

    private Image image;

    private VBox vBox;

    // 0 文本信息 1 文件信息
    private int type;
}
