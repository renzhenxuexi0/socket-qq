package com.client.service;

import com.client.pojo.Result;
import com.client.utils.GetResultUtil;

import java.io.IOException;
import java.util.Properties;

public class UserService {
    private static final String serverIP;
    private static final int serverPort;

    // 静态初始化内容 加载配置文件内容
    static {
        Properties properties = new Properties();
        try {
            properties.load(UserService.class.getResourceAsStream("server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverIP = String.valueOf(properties.get("serverIP"));
        serverPort = Integer.parseInt(properties.get("serverPort").toString());
    }


    /**
     * 客户端完成用户注册的操作
     * 创建一个tcp连接服务端完成注册后接收到注册成功消息后关闭连接
     *
     * @param result
     */
    public Result userRegister(Result result) {
        return GetResultUtil.getResult(result, serverIP, serverPort);
    }

    /**
     * 用户登录：发送信息给服务端，然后接受返回的信息。
     *
     * @param result
     * @return
     */
    public Result userLogin(Result result) {
        return GetResultUtil.getResult(result, serverIP, serverPort);
    }


    /**
     * 获取所有User对象
     *
     * @param result
     * @return
     */
    public Result getAllUser(Result result) {
        return GetResultUtil.getResult(result, serverIP, serverPort);
    }
}

