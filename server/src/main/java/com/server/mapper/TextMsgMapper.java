package com.server.mapper;

import com.server.pojo.TextMsg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TextMsgMapper {

    void addTextMsg(TextMsg textMsg);

    List<TextMsg> selectByReceiveIdOrSenderId(@Param("id") Integer id);
}
