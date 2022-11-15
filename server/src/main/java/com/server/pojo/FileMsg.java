package com.server.pojo;


import lombok.Data;

@Data
public class FileMsg {
    private Integer id;
    private String messageTime;
    private String fileAddress;
    private Integer senderId;
    private Integer receiveId;
}
