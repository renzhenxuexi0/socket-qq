package com.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.pojo.Data;

import java.io.*;
import java.net.Socket;

public class GetDataUtil {
    /**
     * 通用发送给服务器消息模板
     *
     * @param data
     * @param ip
     * @param port
     * @return
     */
    public static Data getData(Data data, String ip, int port) {
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
