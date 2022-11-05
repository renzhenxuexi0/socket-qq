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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getReceiveId() {
        return receiveId;
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