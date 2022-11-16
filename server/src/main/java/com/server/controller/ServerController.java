package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.Code;
import com.server.pojo.Result;
import com.server.pojo.TextMsg;
import com.server.pojo.User;
import com.server.service.TextMsgService;
import com.server.service.UserService;
import com.server.utils.UserMemory;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@EnableScheduling
@Slf4j
public class ServerController {
    // 自动注入线程池
    @Autowired
    private ThreadPoolExecutor pool;
    @Autowired
    private UserService userService;

    @Autowired
    private TextMsgService textMsgService;

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
                    // 打印流 主要打印json字符串
                    PrintStream printStream = null;

                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        InputStream inputStream = socket.getInputStream();

                        // 包装的一些字符流 用来读文本数据
                        printStream = new PrintStream(outputStream);
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream));

                        JSONObject jsonObject = JSON.parseObject(bfr.readLine());
                        System.out.println(jsonObject);

                        // 数据传给服务层
                        Integer code = Integer.valueOf(jsonObject.get("code").toString());

                        if (Code.USER_REGISTER.equals(code)) {
                            // 注册操作
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            Result register = register(user);
                            printStream.println(JSON.toJSONString(register));
                        } else if (Code.USER_LOGIN.equals(code)) {
                            // 登录操作
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            // 对地址进行切割分成ip和端口
                            String s = socket.getRemoteSocketAddress().toString();
                            String[] s1 = s.split(":");
                            String ip = s1[0].substring(1);
                            Integer port = Integer.valueOf(s1[1]);

                            Result login = login(user, ip, port);
                            printStream.println(JSON.toJSONString(login));
                        } else if (Code.GET_ALL_USERS.equals(code)) {
                            // 获取所有用户信息操作
                            Result allUser = getAllUser();
                            printStream.println(JSON.toJSONString(allUser));
                        } else if (Code.OFF_LINE.equals(code)) {
                            // 下线操作
                            User user = JSON.parseObject(jsonObject.get("object").toString(), User.class);
                            offLine(user);
                            printStream.println("");
                        } else if (Code.SEND_OFFLINE_TEXT_MSG.equals(code)) {
                            // 发送文本信息操作
                            TextMsg textMsg = JSON.parseObject(jsonObject.get("object").toString(), TextMsg.class);
                            Result result = sendOffLineTextMsg(textMsg);
                            printStream.println(JSON.toJSONString(result));
                        } else {
                            // 没有找到匹配的操作码操作
                            Result result = new Result();
                            result.setMsg("未知错误");
                            printStream.println(JSON.toJSONString(result));
                        }

                    } catch (IOException e) {
                        // 错误操作
                        Result result = new Result();
                        result.setMsg("未知错误");
                        Objects.requireNonNull(printStream).println(JSON.toJSONString(result));
                        log.error(e.toString());
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

    Result login(User user, String ip, Integer port) {
        User user2 = userService.userLogin(user);

        userService.updateIpAndPort(user2.getId(), ip, port);

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
            List<TextMsg> aboutReceiveTextMsg = textMsgService.findAboutReceiveTextMsg(user2.getId());

            HashMap<String, Object> allContent = new HashMap<>();
            allContent.put("msg", aboutReceiveTextMsg);
            allContent.put("users", UserMemory.users);
            allContent.put("myUser", user2);

            result.setObject(allContent);

            contentInput.appendText(user2.getUsername() + "登录成功\n");
        } else {
            result.setCode(Code.LOGIN_FAIL);
            result.setMsg("账号或密码错误！");
            contentInput.appendText(user.getAccount() + "登录失败\n");
        }
        return result;
    }

    Result getAllUser() {
        Result result = new Result();
        result.setObject(UserMemory.users);
        contentInput.appendText("所有用户信息:\n" + UserMemory.users.toString() + "\n");
        return result;
    }


    @Scheduled(cron = "0/2 * * * * ?")
    void getAllUserScheduled() {
        // 打算做个优化 服务端自己不断获取就行， 返回内存里的用户列表即可
        // 防止请求很多导致数据不同步问题
        UserMemory.users = userService.selectAllUser();
    }

    void offLine(User user) {
        userService.updateLogin(user.getId(), 0);
        contentInput.appendText(user.getUsername() + "下线\n");
    }

    Result sendOffLineTextMsg(TextMsg textMsg) {
        Result result = new Result();
        boolean b = textMsgService.CacheTextMsg(textMsg);
        if (b) {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_SUCCESS);
            contentInput.appendText(textMsg.getSenderId() + "该用户缓存信息成功\n");
        } else {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_FAIL);
            contentInput.appendText(textMsg.getSenderId() + "该用户缓存信息失败\n");
        }
        return result;
    }


}
