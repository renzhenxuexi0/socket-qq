package com.client.service;

import com.client.pojo.Result;
import com.client.utils.GetResultUtil;
import org.springframework.beans.factory.annotation.Value;

public class MsgService {
    @Value("${socket.ip}")
    private String socketIP;
    @Value("${socket.port}")
    private int socketPort;

    /**
     * 通过服务器转发文本消息
     * data里封装msg消息
     *
     * @param result
     * @return
     */
    public Result sendMsgByServer(Result result) {
        return GetResultUtil.getResult(result, socketIP, socketPort);
    }
}
