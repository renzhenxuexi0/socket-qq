package com.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class FileMsgService {
    @Autowired
    ThreadPoolExecutor poolExecutor;
    @Value("${socket.ip}")
    private String socketIP;
    @Value("${socket.port}")
    private int socketPort;

    /**
     * 离线信息的socket
     *
     * @return 返回result对象
     */
    public Socket sendOfflineFileMsg() {
        try {
            Socket socket = new Socket(socketIP, socketPort);
            socket.setTcpNoDelay(true);
            return socket;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }

    /**
     * 发送在线信息的socket
     *
     * @return
     */
    public Socket sendOfflineFileMsg(String ip, Integer port) {
        try {
            Socket socket = new Socket(ip, port);
            socket.setTcpNoDelay(true);
            return socket;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
}
