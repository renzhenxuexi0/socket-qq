package com.client.pojo;

public class Msg {
    private Integer id;
    private String messageTime;
    private String content;
    private String contentType;
    private Integer senderId;
    private Integer receiveId;

    public Integer getId() {
        return id;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getReceiveId() {
        return receiveId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setReceiveId(Integer receiveId) {
        this.receiveId = receiveId;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "msg{" +
                "id=" + id +
                ", messageTime='" + messageTime + '\'' +
                ", content='" + content + '\'' +
                ", contentType='" + contentType + '\'' +
                ", senderId=" + senderId +
                ", receiveId=" + receiveId +
                '}';

    }
}