package com.server.service;

import com.server.pojo.FileMsg;

import java.util.List;

public interface FileMsgService {
    /**
     * 如果对方不在线 则进行文件消息缓存
     *
     * @param fileMsg
     */
    boolean addFileMsg(FileMsg fileMsg);

    /**
     * 查找关于接受者的信息
     *
     * @param id
     * @return
     */
    List<FileMsg> findAboutReceiveOrSenderIdFileMsg(Integer id);

    void updateFileMsgSign(Integer sign, Integer id);
}
