package com.server.mapper;

import com.server.pojo.Msg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MsgMapper {

    void addMsg(Msg msg);

    List<Msg> selectByReceiveId(@Param("receiveId") Integer receiveId);
}
