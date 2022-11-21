package com.client.service;

import com.client.pojo.Code;
import com.client.pojo.Result;
import com.client.utils.GetResultUtil;
import com.client.utils.UserMemory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
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

    @Override
    public void destroy() {
        Result result = new Result();
        result.setCode(Code.OFF_LINE);
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("myUser", UserMemory.myUser);
        stringObjectHashMap.put("textMsg", UserMemory.textMsgList);
        stringObjectHashMap.put("fileMsg", UserMemory.fileMsgList);
        result.setObject(stringObjectHashMap);
        System.out.println(result);
        userOffLine(result);
    }
}

