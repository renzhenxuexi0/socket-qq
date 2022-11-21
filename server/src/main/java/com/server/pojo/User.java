package com.server.pojo;

import lombok.Data;

// 使用该注解自动产生get set toString方法
@Data
public class User {
    private Integer id;
    private String username;
    private String account;
    private String password;
    private Integer login;
    private String ip;
}
