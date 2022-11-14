package com.server.service.impl;

import com.server.mapper.MsgMapper;
import com.server.pojo.Msg;
import com.server.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MsgMapper msgMapper;

    @Override
    public void CacheMsg(Msg msg) {
        msgMapper.addMsg(msg);
    }
}
