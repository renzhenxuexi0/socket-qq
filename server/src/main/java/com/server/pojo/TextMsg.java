package com.server.pojo;

import lombok.Data;

@Data
public class TextMsg {
    private Integer id;
    private String messageTime;
    private String content;
    private Integer senderId;
    private Integer receiveId;
}