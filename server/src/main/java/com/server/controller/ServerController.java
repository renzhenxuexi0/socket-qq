package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.Code;
import com.server.pojo.Data;
import com.server.pojo.User;
import com.server.service.UserService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerController {
    private static final UserService userService = new UserService();

    private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4,
            2, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    public void userServerController() {
        try {
            ServerSocket serverSocket = new ServerSocket(6666);
            while (true) {
                Socket socket = serverSocket.accept();
                // 流的封装
                OutputStream os = socket.getOutputStream();
                PrintStream ps = new PrintStream(os);
                InputStream is = socket.getInputStream();
                Reader reader = new InputStreamReader(is);
                BufferedReader bfr = new BufferedReader(reader);
                JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                System.out.println(jsonObject);
                // 数据传给服务层
                userService.setData(JSON.parseObject(jsonObject.get("object").toString(), User.class));

                if (Code.USER_REGISTER.equals(jsonObject.get("code"))){
                    Future<Boolean> booleanFuture = pool.submit(userService.userRegister());
                    Boolean flag = booleanFuture.get();
                    // 判断是否成功
                    Data data2 = new Data();
                    if (flag.equals(true)){
                        // 再返回数据给客户端
                        data2.setCode(Code.REGISTER_SUCCESS);
                        data2.setMsg("注册成功");
                    }else {
                        data2.setCode(Code.REGISTER_FAIL);
                        data2.setMsg("注册失败");
                    }
                    ps.println(JSON.toJSONString(data2));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
