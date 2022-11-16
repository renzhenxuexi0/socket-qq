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
     * @return 返回result对象
     */
    public Socket sendOfflineFileMsg() {
        try {
            return new Socket(socketIP, socketPort);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return null;
    }
}
