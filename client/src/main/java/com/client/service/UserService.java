package com.client.service;

import com.client.pojo.Data;
import com.client.utils.GetDataUtil;

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
     * @param data
     */
    public Data userRegister(Data data) {
        return GetDataUtil.getData(data, serverIP, serverPort);
    }

    /**
     * 用户登录：发送信息给服务端，然后接受返回的信息。
     *
     * @param data
     * @return
     */
    public Data userLogin(Data data) {
        return GetDataUtil.getData(data, serverIP, serverPort);
    }


    /**
     * 获取所有User对象
     *
     * @param data
     * @return
     */
    public Data getAllUser(Data data) {
        return GetDataUtil.getData(data, serverIP, serverPort);
    }
}

