package com.server.service;

import com.server.pojo.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Callable;

// 开启事务处理
@Transactional
public interface UserService {


    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    Callable<Boolean> userRegister(User user);

    /**
     * 用户登录
     *
     * @return
     */
    public Callable<List<User>> selectAllUser();

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    public Callable<User> userLogin(User user);

    /**
     * 更新在线状态
     *
     * @param id
     * @param login
     * @return
     */
    public Runnable updateLogin(Integer id, Integer login);
}