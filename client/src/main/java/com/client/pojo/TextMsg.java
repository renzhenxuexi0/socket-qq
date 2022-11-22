package com.client.pojo;

import lombok.Data;

@Data
public class TextMsg {
    private Integer id;
    private String messageTime;
    private String content;
    private Integer senderId;
    private Integer receiverId;
}