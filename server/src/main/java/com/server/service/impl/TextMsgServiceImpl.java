package com.server.service.impl;

import com.server.mapper.TextMsgMapper;
import com.server.pojo.TextMsg;
import com.server.service.TextMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TextMsgServiceImpl implements TextMsgService {

    @Autowired
    private TextMsgMapper textMsgMapper;

    @Override
    public boolean addTextMsg(TextMsg textMsg) {
        try {
            textMsgMapper.addTextMsg(textMsg);
            return true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    @Override
    public List<TextMsg> findAboutReceiveOrSenderIdTextMsg(Integer id) {
        return textMsgMapper.selectByReceiveIdOrSenderId(id);
    }

}
