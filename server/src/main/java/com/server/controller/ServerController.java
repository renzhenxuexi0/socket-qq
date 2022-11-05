package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.Code;
import com.server.pojo.Data;
import com.server.pojo.User;
import com.server.service.UserService;
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
    @FXML
    private Button startServerButton;
    @FXML
    private Button viewAllUserButton;
    @FXML
    private TextArea contentInput;
    private String content = "";
    private final Thread start = new Thread(new Runnable() {
        @Override
        public void run() {
            startServer();
        }
    });

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
    void viewAllUserButtonEvent(ActionEvent event) {
        Callable<List<User>> listCallable = userService.selectAllUser();
        Future<List<User>> submit = pool.submit(listCallable);
        try {
            List<User> users = submit.get();
            content += users.toString() + "\n";
            contentInput.setText(content);
        } catch (Exception e) {
            content += "未知错误" + "\n";
            contentInput.setText(content);
        }
    }

    void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
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

                if (Code.USER_REGISTER.equals(jsonObject.get("code"))) {
                    Data register = register(user);
                    ps.println(JSON.toJSONString(register));
                }
                if (Code.USER_LOGIN.equals(jsonObject.get("code"))) {
                    Data login = login(user);
                    ps.println(JSON.toJSONString(login));
                } else {
                    Data data = new Data();
                    data.setMsg("未知错误");
                    ps.println(JSON.toJSONString(data));
                }

            }

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
}
