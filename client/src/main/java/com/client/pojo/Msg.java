package com.client.pojo;

import lombok.Data;

@Data
public class Msg<T> {
    private String username;
    private T msg;
}
