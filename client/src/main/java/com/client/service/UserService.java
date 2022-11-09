package com.client.service;

import com.client.pojo.Result;
import com.client.utils.GetResultUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
public class UserService {
    @Value("${socket.ip}")
    private String socketIP;
    @Value("${socket.port}")
    private int socketPort;


    /**
     * 客户端完成用户注册的操作
     * 创建一个tcp连接服务端完成注册后接收到注册成功消息后关闭连接
     *
     * @param result
     */
    public Result userRegister(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }

    /**
     * 用户登录：发送信息给服务端，然后接受返回的信息。
     *
     * @param result
     * @return
     */
    public Result userLogin(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }


    /**
     * 获取所有User对象
     *
     * @param result
     * @return
     */
    public Result getAllUser(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }
}

