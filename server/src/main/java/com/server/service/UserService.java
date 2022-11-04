package com.server.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserService {
    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

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
                        String msg;
                        while ((msg = bfr.readLine()) != null) {
                            System.out.println(msg);
                        }
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