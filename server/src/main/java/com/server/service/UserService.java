package com.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.pojo.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserService {
    // 创建线程池
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    // json工具
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 服务端完成用户注册的操作
     */
    public void userRegister() {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(() -> {
                    try {
                        InputStream is = socket.getInputStream();
                        Reader reader = new InputStreamReader(is);
                        BufferedReader bfr = new BufferedReader(reader);
                        String json =  bfr.readLine();
                        User user = objectMapper.readValue(json, User.class);
                        System.out.println(user);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        UserService userService = new UserService();
        userService.userRegister();
    }
}