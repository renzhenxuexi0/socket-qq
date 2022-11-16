package com.server.pojo;

import lombok.Data;

@Data
public class Msg<T> {
    private T msg;
}
