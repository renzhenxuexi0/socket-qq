package com.server.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.pojo.*;
import com.server.service.FileMsgService;
import com.server.service.TextMsgService;
import com.server.service.UserService;
import com.server.utils.UserMemory;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

@FXMLController
@EnableScheduling
public class ServerController implements Initializable {
    // 自动注入线程池
    @Autowired
    private ThreadPoolExecutor pool;
    @Autowired
    private UserService userService;

    @Autowired
    private TextMsgService textMsgService;

    @Autowired
    private FileMsgService fileMsgService;

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
                        socket.setTcpNoDelay(true);
                        socket.setSoTimeout(5000);
                        os = socket.getOutputStream();
                        PrintStream ps = new PrintStream(os);
                        InputStream is = socket.getInputStream();//面向字节的输入流抽象类
                        Reader reader = new InputStreamReader(is);//创建面向字节的字符流
                        BufferedReader bfr = new BufferedReader(reader);//从字符流中读取文本，缓存

                        JSONObject jsonObject = JSON.parseObject(bfr.readLine());

                        // 数据传给服务层
                        Integer code = Integer.valueOf(jsonObject.getString("code"));


                        if (Code.USER_REGISTER.equals(code)) {
                            User user = JSON.parseObject(jsonObject.getString("object"), User.class);
                            Result register = register(user);
                            ps.println(JSON.toJSONString(register));
                        } else if (Code.USER_LOGIN.equals(code)) {
                            User user = JSON.parseObject(jsonObject.getString("object"), User.class);
                            Result login = login(user);
                            ps.println(JSON.toJSONString(login));
                        } else if (Code.GET_ALL_USERS.equals(code)) {
                            Result allUser = getAllUser();
                            ps.println(JSON.toJSONString(allUser));
                        } else if (Code.OFF_LINE.equals(code)) {
                            JSONObject jsonObject2 = JSON.parseObject(jsonObject.getString("object"));
                            offLine(jsonObject2);
                            ps.println();
                        } else if (Code.SEND_OFFLINE_TEXT_MSG.equals(code)) {
                            TextMsg textMsg = JSON.parseObject(jsonObject.getString("object"), TextMsg.class);
                            Result result = sendOffLineTextMsg(textMsg);
                            ps.println(JSON.toJSONString(result));
                        } else if (Code.SEND_OFFLINE_FILE_MSG.equals(code)) {
                            FileMsg fileMsg = JSON.parseObject(jsonObject.getString("object"), FileMsg.class);
                            receiveFileMsg(fileMsg, socket);
                        } else if (Code.RECEIVE_OFFLINE_FILE_MSG.equals(code)) {
                            FileMsg fileMsg = JSON.parseObject(jsonObject.getString("object"), FileMsg.class);
                            sendFileMsg(fileMsg, socket);
                        } else if (Code.SEND_GROUP_OFFLINE_FILE_MSG.equals(code)) {
                            List<FileMsg> fileMsgList = JSON.parseArray(jsonObject.getString("object"), FileMsg.class);
                            receiveGroupFileMsg(fileMsgList, socket);
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
        //判断是否成功
        Result result = new Result();
        if (user2 != null) {
            if (user2.getLogin().equals(1)) {
                result.setCode(Code.LOGIN_FAIL);
                result.setMsg("账号已经登录了");
            } else {
                UserMemory.users = userService.selectAllUser();
                // 设置登录状态
                userService.updateLogin(user2.getId(), 1);

                userService.updateIp(user2.getId(), user.getIp());

                //返回数据给客户端
                result.setCode(Code.LOGIN_SUCCESS);
                result.setMsg("登录成功");

                // 查找关于自己的离线信息
                List<TextMsg> aboutReceiveTextMsg = textMsgService.findAboutReceiveOrSenderIdTextMsg(user2.getId());
                List<FileMsg> aboutReceiveFileMsg = fileMsgService.findAboutReceiveOrSenderIdFileMsg(user2.getId());

                HashMap<String, Object> allContent = new HashMap<>();
                allContent.put("textMsg", aboutReceiveTextMsg);
                allContent.put("fileMsg", aboutReceiveFileMsg);
                allContent.put("users", UserMemory.users);
                allContent.put("myUser", user2);


                result.setObject(allContent);

                contentInput.appendText(user2.getUsername() + "登录成功\n");
            }

        } else {
            result.setCode(Code.LOGIN_FAIL);
            result.setMsg("登录失败");
            contentInput.appendText(user.getAccount() + "登录失败\n");
        }
        return result;
    }

    Result getAllUser() {
        Result result = new Result();
        result.setObject(UserMemory.users);
        return result;
    }


    @Scheduled(cron = "0/2 * * * * ?")
    void getAllUserScheduled() {
        // 打算做个优化 服务端自己不断获取就行， 返回内存里的用户列表即可
        // 防止请求很多导致数据不同步问题
        UserMemory.users = userService.selectAllUser();
    }

    void offLine(JSONObject jsonObject) {
        User user = JSON.parseObject(jsonObject.getString("myUser"), User.class);
        List<TextMsg> textMsgList = JSON.parseArray(jsonObject.getString("textMsg"), TextMsg.class);
        List<FileMsg> fileMsgList = JSON.parseArray(jsonObject.getString("fileMsg"), FileMsg.class);
        userService.updateLogin(user.getId(), 0);

        textMsgList.forEach(textMsg -> textMsgService.addTextMsg(textMsg));
        fileMsgList.forEach(fileMsg -> fileMsgService.addFileMsg(fileMsg));

        contentInput.appendText(user.getUsername() + "下线\n");
    }

    Result sendOffLineTextMsg(TextMsg textMsg) {
        Result result = new Result();
        boolean b = textMsgService.addTextMsg(textMsg);
        if (b) {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_SUCCESS);
            contentInput.appendText(textMsg.getSenderId() + "该用户缓存信息成功\n");
        } else {
            result.setCode(Code.SEND_OFFLINE_TEXT_MSG_FAIL);
            contentInput.appendText(textMsg.getSenderId() + "该用户缓存信息失败\n");
        }
        return result;
    }

    void receiveFileMsg(FileMsg fileMsg, Socket socket) {
        String[] split = fileMsg.getFileName().split("\\.");
        String nameSuffix = split[split.length - 1];
        File file = new File(System.getProperty("user.home") + "\\.serverSocketFiles\\" + System.currentTimeMillis() + "." + nameSuffix);
        fileMsg.setFileAddress(System.getProperty("user.home") + "\\.serverSocketFiles\\" + System.currentTimeMillis() + "." + nameSuffix);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            try {
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (RandomAccessFile rw = new RandomAccessFile(file, "rw")) {
            rw.seek(fileMsg.getStartPoint());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] bytes = new byte[(int) (fileMsg.getSize() / 5)];
            int len;
            long accumulationSize = fileMsg.getStartPoint();
            while ((len = dataInputStream.read(bytes)) != -1) {
                rw.write(bytes, 0, len);
                accumulationSize += len;
            }
            if (accumulationSize == fileMsg.getSize()) {
                contentInput.appendText("接受文件" + fileMsg.getFileName() + "成功\n");
                fileMsg.setEndPoint(accumulationSize);
                fileMsgService.addFileMsg(fileMsg);
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void receiveGroupFileMsg(List<FileMsg> fileMsgList, Socket socket) {

        String[] split = fileMsgList.get(0).getFileName().split("\\.");
        String nameSuffix = split[split.length - 1];
        File file = new File(System.getProperty("user.home") + "\\.serverSocketFiles\\" + System.currentTimeMillis() + "." + nameSuffix);
        for (FileMsg fileMsg : fileMsgList) {
            fileMsg.setFileAddress(System.getProperty("user.home") + "\\.serverSocketFiles\\" + System.currentTimeMillis() + "." + nameSuffix);
        }

        if (!file.exists()) {
            File parentFile = file.getParentFile();
            try {
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (RandomAccessFile rw = new RandomAccessFile(file, "rw")) {
            socket.setTcpNoDelay(true);
            Long startPoint = fileMsgList.get(0).getStartPoint();
            rw.seek(startPoint);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] bytes = new byte[(int) (fileMsgList.get(0).getSize() / 5)];
            int len;
            long accumulationSize = startPoint;
            while ((len = dataInputStream.read(bytes)) != -1) {
                rw.write(bytes, 0, len);
                accumulationSize += len;
                if (accumulationSize == fileMsgList.get(0).getSize()) {
                    socket.close();
                    break;
                }
            }
            for (FileMsg fileMsg : fileMsgList) {
                fileMsg.setEndPoint(accumulationSize);
                fileMsgService.addFileMsg(fileMsg);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void sendFileMsg(FileMsg fileMsg, Socket socket) {

        File file = new File(fileMsg.getFileAddress());

        try (RandomAccessFile rw = new RandomAccessFile(file, "r")) {
            socket.setTcpNoDelay(true);
            rw.seek(fileMsg.getStartPoint());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            byte[] bytes = new byte[(int) (fileMsg.getSize() / 5)];
            int len;
            while ((len = rw.read(bytes)) != -1) {
                Thread.sleep(1000);
                dataOutputStream.write(bytes, 0, len);
                dataOutputStream.flush();
            }
            dataOutputStream.flush();
            socket.isOutputShutdown();
            fileMsgService.updateFileMsgSign(1, fileMsg.getId());
            contentInput.appendText("发送文件" + fileMsg.getFileName() + "成功\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
