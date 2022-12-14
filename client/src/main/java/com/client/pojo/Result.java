package com.client.pojo;

import lombok.Data;

@Data
public class Result {
    // 编码
    private Integer code;
    // 数据库实体类对象
    private Object object;
    // 一些信息
    private String msg;
}
