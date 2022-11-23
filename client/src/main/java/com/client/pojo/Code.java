package com.client.pojo;

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


    // 开始发送离线文件
    public static final Integer SEND_OFFLINE_FILE_MSG = 6001;
    // 接收离线文件
    public static final Integer RECEIVE_OFFLINE_FILE_MSG = 6002;


    // 开始视频通话
    public static final Integer START_VIDEO_CHAT = 7001;
    // 同意视频通话
    public static final Integer CONSENT_VIDEO_CHAT = 7002;
    // 拒绝视频通话
    public static final Integer REFUSE_VIDEO_CHAT = 7003;

    // 获取所有用户信息
    public static final Integer GET_ALL_USERS = 8001;

    // 开始群发离线文件
    public static final Integer SEND_GROUP_OFFLINE_FILE_MSG = 9001;
    // 接收群发离线文件
    public static final Integer RECEIVE_GROUP_OFFLINE_FILE_MSG = 9002;

    // 下线
    public static final Integer OFF_LINE = 10001;


}

