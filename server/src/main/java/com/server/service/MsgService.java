package com.server.service;

import com.server.pojo.Msg;

public interface MsgService {

    /**
     * 如果对方不在线 则进行消息缓存
     *
     * @param msg
     */
    void CacheMsg(Msg msg);
}
