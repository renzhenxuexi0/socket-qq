package com.client.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.pojo.Data;

import java.io.*;
import java.net.Socket;
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
     * @param data
     */
    public Data userRegister(Data data) {
        return getData(data, serverIP, serverPort);
    }

    /**
     * 用户登录：发送信息给服务端，然后接受返回的信息。
     * @param data
     * @return
     */
    public Data userLogin(Data data) {
        return getData(data, serverIP, serverPort);
    }

    /**
     * 通过服务器转发文本消息
     * data里封装msg消息
     * @param data
     * @return
     */
    public Data sendMsgByServer(Data data){
        return getData(data, serverIP, serverPort);
    }


    /**
     * 获取所有User对象
     * @param data
     * @return
     */
    public Data getAllUser(Data data){
        return getData(data, serverIP, serverPort);
    }


    /**
     * 通用发送给服务器消息模板
     * @param data
     * @param ip
     * @param port
     * @return
     */
    private Data getData(Data data, String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            // 一些流的封装
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            InputStream is = socket.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader bfr = new BufferedReader(reader);
            // 登陆信息
            String json = JSON.toJSONString(data, SerializerFeature.WriteMapNullValue);
            System.out.println(json);
            ps.println(json);
            ps.flush();
            // 服务端返回的信息
            String json2 = bfr.readLine();
            Data data2 = JSON.parseObject(json2, Data.class);
            // 如果成功则关闭连接 给控制器返回一个true
            socket.close();
            return data2;
        } catch (Exception e) {
            e.printStackTrace();
            // 出错报错
            Data data3 = new Data();
            data3.setMsg("未知错误");
            return data3;
        }
    }
}

