package com.client.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.client.pojo.Result;

import java.io.*;
import java.net.Socket;

public class GetResultUtil {
    /**
     * 通用发送给服务器消息模板
     *
     * @param result
     * @param ip
     * @param port
     * @return
     */
    public static Result getResult(Result result, String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            // 一些流的封装
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            InputStream is = socket.getInputStream();
            Reader reader = new InputStreamReader(is);
            BufferedReader bfr = new BufferedReader(reader);
            // 登陆信息
            String json = JSON.toJSONString(result, SerializerFeature.WriteMapNullValue);
            System.out.println(json);
            ps.println(json);
            ps.flush();
            // 服务端返回的信息
            String json2 = bfr.readLine();
            Result result2 = JSON.parseObject(json2, Result.class);
            // 如果成功则关闭连接 给控制器返回一个true
            socket.close();
            return result2;
        } catch (Exception e) {
            e.printStackTrace();
            // 出错报错
            Result result3 = new Result();
            result3.setMsg("未知错误");
            return result3;
        }
    }
}
