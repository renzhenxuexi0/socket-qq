package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.Code;
import com.server.pojo.Data;
import com.server.pojo.User;
import com.server.service.UserService;
import com.server.utils.UserMemory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class ServerController {
    private static final UserService userService = new UserService();

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    // 所有信息
    private String content = "";

    // 服务端运行线程
    private final Thread start = new Thread(new Runnable() {
        @Override
        public void run() {
            startServer();
        }
    });

    @FXML
    private Button closeServerButton;

    @FXML
    private Button startServerButton;


    @FXML
    private TextArea contentInput;

    /**
     * 创建线程启动服务
     */
    @FXML
    void startServerButtonEvent(ActionEvent event) {
        start.start();
        content += "服务器启动" + "\n";
        contentInput.setText(content);
    }

    @FXML
    void closeServerButtonEvent(ActionEvent event) {
        // 调用该方法给线程打上中止标记
        start.interrupt();
    }

    void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            do {
                Socket socket = serverSocket.accept();
                // 流的封装
                OutputStream os = socket.getOutputStream();//字节输出流抽象类
                PrintStream ps = new PrintStream(os);
                InputStream is = socket.getInputStream();//面向字节的输入流抽象类
                Reader reader = new InputStreamReader(is);//创建面向字节的字符流
                BufferedReader bfr = new BufferedReader(reader);//从字符流中读取文本，缓存
                JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                System.out.println(jsonObject);

                // 数据传给服务层
                User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                Integer code = Integer.valueOf(jsonObject.get("code").toString());

                if (Code.USER_REGISTER.equals(code)) {
                    Data register = register(user);
                    ps.println(JSON.toJSONString(register));
                }
                if (Code.USER_LOGIN.equals(code)) {
                    Data login = login(user);
                    ps.println(JSON.toJSONString(login));
                }
                if (Code.GET_USERS.equals(code)) {
                    Data allUser = getAllUser();
                    ps.println(JSON.toJSONString(allUser));
                } else {
                    Data data = new Data();
                    data.setMsg("未知错误");
                    ps.println(JSON.toJSONString(data));
                }
                // 判读线程是否调用Interrupted，给线程打上中止标记 打上就退出循环
            } while (!Thread.currentThread().isInterrupted());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Data register(User user) throws ExecutionException, InterruptedException {
        Future<Boolean> booleanFuture = pool.submit(userService.userRegister(user));
        Boolean flag = booleanFuture.get();
        // 判断是否成功
        Data data = new Data();
        if (flag.equals(true)) {
            // 再返回数据给客户端
            data.setCode(Code.REGISTER_SUCCESS);
            data.setMsg("注册成功");
            content += user.getUsername() + "注册成功" + "\n";
            contentInput.setText(content);
        } else {
            data.setCode(Code.REGISTER_FAIL);
            data.setMsg("注册失败");
            content += user.getUsername() + "注册失败" + "\n";
            contentInput.setText(content);
        }
        return data;
    }

    Data login(User user) throws Exception {
        Future<User> userFuture = pool.submit(userService.userLogin(user));
        User user2 = userFuture.get();
        //判断是否成功
        Data data = new Data();
        if (user2 != null) {
            //返回数据给客户端
            data.setCode(Code.LOGIN_SUCCESS);
            data.setMsg("登录成功");
            content += user2.getUsername() + "登录成功" + "\n";
            contentInput.setText(content);
        } else {
            data.setCode(Code.LOGIN_FAIL);
            data.setMsg("登录失败");
            content += "登录失败" + "\n";
            contentInput.setText(content);
        }
        return data;
    }

    Data getAllUser() throws Exception {
        Future<List<User>> userFuture = pool.submit(userService.selectAllUser());
        List<User> users = userFuture.get();
        UserMemory.users = users;
        //判断是否成功
        Data data = new Data();
        data.setCode(Code.GET_SUCCESS);
        data.setObject(users);
        return data;
    }
}
