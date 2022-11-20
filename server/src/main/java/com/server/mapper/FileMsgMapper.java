package com.server.mapper;

import com.server.pojo.FileMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMsgMapper {
    void addFileMsg(FileMsg fileMsg);

    List<FileMsg> selectByReceiveId(@Param("receiveId") Integer receiveId);

    void updateSignById(@Param("sign") Integer sign, @Param("id") Integer id);
}
