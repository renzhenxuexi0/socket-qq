package com.server.service;

import com.server.pojo.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserService {
//    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
//            2, TimeUnit.SECONDS,
//            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.AbortPolicy());

    /**
     * 服务端完成用户注册的操作
     */
    public void userRegister() throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        Socket accept = serverSocket.accept();
        InputStream inputStream = accept.getInputStream();
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bfr = new BufferedReader(reader);
        String msg;
        while ((msg = bfr.readLine()) != null) {
            System.out.println(msg);
        }
//        while (true) {
//            pool.execute(() -> {
//                try {
//                    Socket socket = serverSocket.accept();
//                    socket.getInputStream();
//                    InputStream is = socket.getInputStream();
//                    Reader reader = new InputStreamReader(is);
//                    BufferedReader bfr = new BufferedReader(reader);
//
//                    String msg;
//                    while ((msg = bfr.readLine()) != null) {
//                        System.out.println(msg);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
    }

    public static void main(String[] args) throws IOException {
        UserService userService = new UserService();
        userService.userRegister();
    }
}
