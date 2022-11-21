package com.client.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TextMsg extends Msg {
    private Integer id;
    private String messageTime;
    private String content;
    private Integer senderId;
    private Integer receiverId;
}