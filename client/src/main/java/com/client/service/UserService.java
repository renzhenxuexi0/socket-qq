package com.client.service;

import com.client.pojo.Code;
import com.client.pojo.Data;
import com.client.pojo.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.Socket;

public class UserService {
    // json工具
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 客户端完成用户注册的操作
     * 创建一个tcp连接完成注册后接收到注册成功消息后关闭连接
     * @param data
     */

    public Data userRegister(Data data) {
        try {
            Socket socket = new Socket("127.0.0.1", 6666);
            OutputStream os = socket.getOutputStream();
            // 把低级的字节输出流封装成高级打印字节流
            PrintStream ps = new PrintStream(os);
            // 注册信息
            String json = objectMapper.writeValueAsString(data);
            System.out.println(json);
            ps.println(json);
            ps.flush();
            InputStream is = socket.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader bfr = new BufferedReader(reader);
            // 服务端返回的信息
            String json2 =  bfr.readLine();
            Data data2 = objectMapper.readValue(json2, Data.class);
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
