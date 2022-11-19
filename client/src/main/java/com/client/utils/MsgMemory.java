package com.client.utils;

import com.client.pojo.FileMsg;
import com.client.pojo.SendMsg;
import com.client.pojo.TextMsg;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class MsgMemory {
    public static List<SendMsg> sendMsgList = new ArrayList<>();

    public static void sendMsgListSort(SimpleDateFormat simpleDateFormat) {
        sendMsgList.sort((o1, o2) -> {
            try {
                Date date1;
                Date date2;
                if (o1.getType() == 0) {
                    TextMsg msg = (TextMsg) o1.getMsg();
                    date1 = simpleDateFormat.parse(msg.getMessageTime());
                } else {
                    FileMsg msg = (FileMsg) o1.getMsg();
                    date1 = simpleDateFormat.parse(msg.getMessageTime());
                }
                if (o2.getType() == 0) {
                    TextMsg msg = (TextMsg) o2.getMsg();
                    date2 = simpleDateFormat.parse(msg.getMessageTime());
                } else {
                    FileMsg msg = (FileMsg) o2.getMsg();
                    date2 = simpleDateFormat.parse(msg.getMessageTime());
                }
                return date1.compareTo(date2);
            } catch (ParseException e) {
                log.error(e.toString());
            }
            return 0;
        });
    }
}
