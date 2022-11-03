package com.client.service;

import com.client.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class UserService {
    /**
     * 客户端完成用户注册的操作
     * @param user
     */

    public void userRegister(User user){

        ObjectMapper mapper=new ObjectMapper();

        try {
            Socket socket = new Socket("127.0.0.1", 8888);
            OutputStream os = socket.getOutputStream();
            // 把低级的字节输出流封装成高级打印字节流
            PrintStream ps = new PrintStream(os);
            String json = mapper.writeValueAsString(user);
            System.out.println(json);
            ps.println(json);
            ps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
