package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.Code;
import com.server.pojo.Msg;
import com.server.pojo.Result;
import com.server.pojo.User;
import com.server.service.MsgService;
import com.server.service.UserService;
import com.server.utils.UserMemory;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
public class ServerController {
    // 自动注入线程池
    @Autowired
    private ThreadPoolExecutor pool;
    @Autowired
    private UserService userService;

    @Autowired
    private MsgService msgService;

    @FXML
    private Button closeServerButton;
    @FXML
    private Button startServerButton;
    @FXML
    private TextArea contentInput;

    private Thread start;

    /**
     * 创建线程启动服务
     */
    @FXML
    void startServerButtonEvent(ActionEvent event) {
        start = new Thread(this::startServer);
        start.start();
        contentInput.appendText("服务器启动\n");
    }

    @FXML
    void closeServerButtonEvent(ActionEvent event) {
        // 调用该方法给线程打上中止标记， 并创建一个socket连接防止阻塞
        start.interrupt();
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
        contentInput.appendText("服务器关闭\n");
    }

    void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            // 判读线程是否调用Interrupted，给线程打上中止标记 打上就退出循环
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                pool.execute(() -> {
                    // 流的封装
                    OutputStream os;//字节输出流抽象类
                    try {
                        os = socket.getOutputStream();
                        PrintStream ps = new PrintStream(os);
                        InputStream is = socket.getInputStream();//面向字节的输入流抽象类
                        Reader reader = new InputStreamReader(is);//创建面向字节的字符流
                        BufferedReader bfr = new BufferedReader(reader);//从字符流中读取文本，缓存

                        JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                        System.out.println(jsonObject);

                        // 数据传给服务层
                        Integer code = Integer.valueOf(jsonObject.get("code").toString());

                        if (Code.USER_REGISTER.equals(code)) {
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            Result register = register(user);
                            ps.println(JSON.toJSONString(register));
                        }
                        if (Code.USER_LOGIN.equals(code)) {
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            Result login = login(user);
                            ps.println(JSON.toJSONString(login));
                        }
                        if (Code.GET_ALL_USERS.equals(code)) {
                            Result allUser = getAllUser();
                            ps.println(JSON.toJSONString(allUser));
                        } else if (Code.OFF_LINE.equals(code)) {
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            offLine(user);
                            ps.println("");
                        } else if (Code.SEND_OFFLINE_TEXT_MSG.equals(code)) {
                            Msg msg = JSON.parseObject(jsonObject.get("object").toString(), Msg.class);
                            Result result = sendOffLineTextMsg(msg);
                            ps.println(JSON.toJSONString(result));
                        } else {
                            Result result = new Result();
                            result.setMsg("未知错误");
                            ps.println(JSON.toJSONString(result));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Result register(User user) {
        boolean b = userService.userRegister(user);
        // 判断是否成功
        Result result = new Result();
        if (b) {
            // 再返回数据给客户端
            result.setCode(Code.REGISTER_SUCCESS);
            result.setMsg("注册成功");
            contentInput.appendText(user.getUsername() + "注册成功\n");
        } else {
            result.setCode(Code.REGISTER_FAIL);
            result.setMsg("注册失败");
            contentInput.appendText("注册失败\n");
        }
        return result;
    }

    Result login(User user) {
        User user2 = userService.userLogin(user);
        UserMemory.users = userService.selectAllUser();
        //判断是否成功
        Result result = new Result();

        if (user2 != null) {
            // 设置登录状态
            userService.updateLogin(user2.getId(), 1);

            //返回数据给客户端
            result.setCode(Code.LOGIN_SUCCESS);
            result.setMsg("登录成功");
            // 查找关于自己的离线信息
            List<Msg> aboutReceiveMsg = msgService.findAboutReceiveMsg(user2.getId());
            
            HashMap<String, Object> allContent = new HashMap<>();
            allContent.put("msg", aboutReceiveMsg);
            allContent.put("users", UserMemory.users);
            allContent.put("myUser", user2);

            result.setObject(allContent);

            contentInput.appendText(user2.getUsername() + "登录成功\n");
        } else {
            result.setCode(Code.LOGIN_FAIL);
            result.setMsg("登录失败");
            contentInput.appendText(user.getAccount() + "登录失败\n");
        }
        return result;
    }

    Result getAllUser() {
        UserMemory.users = userService.selectAllUser();
        Result result = new Result();
        result.setObject(UserMemory.users);
        contentInput.appendText("所有用户信息:\n" + UserMemory.users.toString() + "\n");
        return result;
    }

    void offLine(User user) {
        userService.updateLogin(user.getId(), 0);
        contentInput.appendText(user.getUsername() + "下线\n");
    }

    Result sendOffLineTextMsg(Msg msg) {
        Result result = new Result();
        boolean b = msgService.CacheMsg(msg);
        if (b) {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_SUCCESS);
            contentInput.appendText(msg.getSenderId() + "该用户缓存信息成功\n");
        } else {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_FAIL);
            contentInput.appendText(msg.getSenderId() + "该用户缓存信息失败\n");
        }
        return result;
    }


}
