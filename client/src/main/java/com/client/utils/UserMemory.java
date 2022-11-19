package com.client.utils;

import com.client.pojo.TextMsg;
import com.client.pojo.User;

import java.util.List;

/**
 * 用来存储用户列表的
 */
public class UserMemory {

    // 自己的用户信息
    public static User myUser;

    // 所有用户列表
    public static List<User> users;

    // 对正在聊天的用户
    public static User talkUser;

    // 离线文本信息
    public static List<TextMsg> textMsgList;
}
