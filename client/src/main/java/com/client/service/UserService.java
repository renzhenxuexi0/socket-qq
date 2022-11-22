package com.client.service;

import com.alibaba.fastjson.JSON;
import com.client.pojo.Code;
import com.client.pojo.FileMsg;
import com.client.pojo.Result;
import com.client.pojo.TextMsg;
import com.client.utils.GetResultUtil;
import com.client.utils.UserMemory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserService implements DisposableBean {
    @Value("${socket.ip}")
    private String socketIP;
    @Value("${socket.port}")
    private int socketPort;


    /**
     * 客户端完成用户注册的操作
     * 创建一个tcp连接服务端完成注册后接收到注册成功消息后关闭连接
     *
     * @param result
     */
    public Result userRegister(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }

    /**
     * 用户登录：发送信息给服务端，然后接受返回的信息。
     *
     * @param result
     * @return
     */
    public Result userLogin(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }


    /**
     * 获取所有User对象
     *
     * @param result
     * @return
     */
    public Result getAllUser(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }


    public void userOffLine(Result result) {
        GetResultUtil.getResult(result, socketIP, socketPort);
    }

    @SneakyThrows
    @Override
    public void destroy() {
        Result result = new Result();
        result.setCode(Code.OFF_LINE);

        List<TextMsg> textMsgList = new ArrayList<>();
        File textLogFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\textMsgLog.txt");

        String[] textLog = FileUtils.readFileToString(textLogFile, StandardCharsets.UTF_8).split("\n");
        if (textLogFile.exists()) {
            for (String s : textLog) {
                if (!"".equals(s)) {
                    textMsgList.add(JSON.parseObject(s, TextMsg.class));
                }
            }

            BufferedWriter textLogWriter = new BufferedWriter(new FileWriter(textLogFile, false));
            textLogWriter.write("");
            textLogWriter.flush();
            textLogWriter.close();
        }


        List<FileMsg> fileMsgList = new ArrayList<>();
        File fileLogFile = new File(System.getProperty("user.home") + "\\.socket\\" + UserMemory.myUser.getAccount() + "\\fileMsgLog.txt");
        if (fileLogFile.exists()) {
            String[] fileLog = FileUtils.readFileToString(fileLogFile, StandardCharsets.UTF_8).split("\n");
            for (String s : fileLog) {
                if (!"".equals(s)) {
                    fileMsgList.add(JSON.parseObject(s, FileMsg.class));
                }
            }

            BufferedWriter fileLogWriter = new BufferedWriter(new FileWriter(fileLogFile, false));
            fileLogWriter.write("");
            fileLogWriter.flush();
            fileLogWriter.close();
        }

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("myUser", UserMemory.myUser);
        stringObjectHashMap.put("textMsg", textMsgList);
        stringObjectHashMap.put("fileMsg", fileMsgList);
        result.setObject(stringObjectHashMap);
        log.info("下线成功");
        userOffLine(result);
    }
}

