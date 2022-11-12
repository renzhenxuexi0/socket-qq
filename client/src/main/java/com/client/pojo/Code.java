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
    // 发送文本信息
    public static final Integer USER_SEND_TEXT_MSG = 3001;
    // 发送文本信息成功
    public static final Integer SEND_TEXT_MSG_SUCCESS = 3002;
    // 发送文本消息失败
    public static final Integer SEND_TEXT_MSG_FAIL = 3003;

    // 获取所有用户信息
    public static final Integer GET_ALL_USERS = 4001;

    // 下线
    public static final Integer OFF_LINE = 5001;


}

