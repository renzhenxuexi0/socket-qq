package com.client.pojo;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class FileMsg extends Msg {
    private Integer id;
    private String fileName;
    private String messageTime;
    private String fileAddress;
    private Integer senderId;
    private Integer receiveId;
    private Long startPoint;
    private Long endPoint;
}
