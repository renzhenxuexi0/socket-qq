package com.client.pojo;


import lombok.Data;

@Data
public class FileMsg {
    private Integer id;
    private String fileName;
    private String messageTime;
    private String fileAddress;
    private Integer senderId;
    private Integer receiverId;
    private Long startPoint;
    private Long endPoint;
    private Integer sign;
    private Integer online;
    private Long size;
}
