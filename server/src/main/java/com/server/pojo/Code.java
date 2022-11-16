package com.server.pojo;

/**
 * 使用编码对操作进行选择
 */
public class Code {
    // 申请注册
    public static final Integer USER_REGISTER = 1001;
    // 注册成功
    public static final Integer REGISTER_SUCCESS = 1002;
    // 注册失败
    public static final Integer REGISTER_FAIL = 1003;

    // 用户登录
    public static final Integer USER_LOGIN = 2001;
    // 登录成功
    public static final Integer LOGIN_SUCCESS = 2002;
    // 登录失败
    public static final Integer LOGIN_FAIL = 2003;

    // 发送在线文本信息
    public static final Integer SEND_TEXT_MSG = 3001;
    // 发送在线文本信息成功
    public static final Integer SEND_TEXT_MSG_SUCCESS = 3002;
    // 发送在线文本消息失败
    public static final Integer SEND_TEXT_MSG_FAIL = 3003;


    // 发送离线文本信息
    public static final Integer SEND_OFFLINE_TEXT_MSG = 4001;
    // 发送离线文本信息成功
    public static final Integer SEND_OFFLINE_TEXT_MSG_SUCCESS = 4002;
    // 发送离线文本消息失败
    public static final Integer SEND_OFFLINE_TEXT_MSG_FAIL = 4003;


    //发送文件信息
    public static final Integer SEND_FILE_MSG = 5001;
    // 同意发送在线文件
    public static final Integer ALLOW_SEND_FILE_MSG = 5002;
    // 拒绝发送在线文件
    public static final Integer NOT_ALLOW_SEND_FILE_MSG = 5003;
    //发送在线文件信息成功
    public static final Integer SEND_FILE_MSG_SUCCESS = 5004;
    //发送在线文件信息失败
    public static final Integer SEND_FILE_MSG_FAIL = 5005;


    //发送离线文件信息
    public static final Integer SEND_OFFLINE_FILE_MSG = 6001;
    // 发送完成离线文件
    public static final Integer SEND_COMPLETED_OFFLINE_FILE_MSG = 6002;
    // 发送离线文件信息成功
    public static final Integer SEND_OFFLINE_FILE_MSG_SUCCESS = 6003;
    // 发送离线文件信息失败
    public static final Integer SEND_OFFLINE_FILE_MSG_FAIL = 6004;


    // 获取所有用户信息
    public static final Integer GET_ALL_USERS = 7001;

    // 下线
    public static final Integer OFF_LINE = 8001;


}

