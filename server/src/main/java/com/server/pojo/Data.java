package com.server.pojo;

public class Data {
    // 编码
    private Integer code;
    // 数据库实体类对象
    private Object object;
    // 一些信息
    private String msg;

    @Override
    public String toString() {
        return "Data{" +
                "code=" + code +
                ", object=" + object +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
