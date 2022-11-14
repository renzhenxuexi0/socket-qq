package com.server.service;

import com.server.pojo.Msg;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface MsgService {

    /**
     * 如果对方不在线 则进行消息缓存
     *
     * @param msg
     */
    boolean CacheMsg(Msg msg);

    /**
     * 查找关于接受者的信息
     *
     * @param receiveId
     * @return
     */
    List<Msg> findAboutReceiveMsg(Integer receiveId);
}
