package com.server.service;

import com.server.pojo.TextMsg;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TextMsgService {

    /**
     * 如果对方不在线 则进行消息缓存
     *
     * @param textMsg
     */
    boolean addTextMsg(TextMsg textMsg);

    /**
     * 查找关于接受者的信息
     *
     * @param id
     * @return
     */
    List<TextMsg> findAboutReceiveOrSenderIdTextMsg(Integer id);
}
