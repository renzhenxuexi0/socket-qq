package com.server.service.impl;

import com.server.mapper.MsgMapper;
import com.server.pojo.Msg;
import com.server.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MsgMapper msgMapper;

    @Override
    public boolean CacheMsg(Msg msg) {
        try {
            msgMapper.addMsg(msg);
            return true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    @Override
    public List<Msg> findAboutReceiveMsg(Integer receiveId) {
        return msgMapper.selectByReceiveId(receiveId);
    }
}
