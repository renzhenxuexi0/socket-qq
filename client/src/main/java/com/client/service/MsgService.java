package com.client.service;

import com.client.pojo.Data;
import com.client.utils.GetDataUtil;

import java.io.IOException;
import java.util.Properties;

public class MsgService {
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
     * 通过服务器转发文本消息
     * data里封装msg消息
     *
     * @param data
     * @return
     */
    public Data sendMsgByServer(Data data) {
        return GetDataUtil.getData(data, serverIP, serverPort);
    }
}
