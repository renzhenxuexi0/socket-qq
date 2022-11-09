package com.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
public class Result {
    // 编码
    private Integer code;
    // 数据库实体类对象
    private Object object;
    // 一些信息
    private String msg;
}
