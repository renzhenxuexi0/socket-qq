package com.qq.pojo;

public class msg {
    private Integer id;

    private String messageTime;

    private String content;

    private Integer contentType;

    private Integer SenderId;

    private Integer receiverId;

    @Override
    public String toString() {
        return "msg{" +
                "id=" + id +
                ", messageTime='" + messageTime + '\'' +
                ", content='" + content + '\'' +
                ", contentType=" + contentType +
                ", SenderId=" + SenderId +
                ", receiverId=" + receiverId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public String getContent() {
        return content;
    }

    public Integer getContentType() {
        return contentType;
    }

    public Integer getSenderId() {
        return SenderId;
    }

    public Integer getReceiverId() {
        return receiverId;
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

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public void setSenderId(Integer senderId) {
        SenderId = senderId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }
}
