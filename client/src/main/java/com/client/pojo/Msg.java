package com.client.pojo;

import lombok.Data;

@Data
public class Msg {
    private Integer id;
    private String messageTime;
    private String content;
    private Integer contentType;
    private Integer senderId;
    private Integer receiveId;
}