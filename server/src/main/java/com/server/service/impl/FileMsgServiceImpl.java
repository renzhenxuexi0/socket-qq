package com.server.service.impl;

import com.server.mapper.FileMsgMapper;

import com.server.pojo.FileMsg;
import com.server.service.FileMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FileMsgServiceImpl implements FileMsgService {
    @Autowired
    private FileMsgMapper fileMsgMapper;

    public boolean CacheFileMsg(FileMsg fileMsg) {
        try {
            fileMsgMapper.addFileMsg(fileMsg);
            return true;
        } catch (Exception e) {
            log.error(e.toString());
        }
        return false;
    }

    public List<FileMsg> findAboutReceiveFileMsg(Integer receiveId) {
        return fileMsgMapper.selectByReceiveId(receiveId);
    }


}
