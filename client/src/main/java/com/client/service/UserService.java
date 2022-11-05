package com.client.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.pojo.Data;

import java.io.*;
import java.net.Socket;

public class UserService {
    /**
     * 客户端完成用户注册的操作
     * 创建一个tcp连接完成注册后接收到注册成功消息后关闭连接
     *
     * @param data
     */

    public Data userRegister(Data data) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            // 一些流的封装
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            InputStream is = socket.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader bfr = new BufferedReader(reader);

            // 注册信息
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
