package com.client.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileMsgService {
    @Value("${socket.ip}")
    private String socketIP;
    @Value("${socket.port}")
    private int socketPort;

}
