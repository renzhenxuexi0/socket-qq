package com.server.mapper;

import com.server.pojo.Msg;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgMapper {

    void addMsg(Msg msg);
}
