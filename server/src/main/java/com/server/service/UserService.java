package com.server.service;

import com.server.pojo.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 开启事务处理
@Transactional
public interface UserService {


    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    boolean userRegister(User user);

    /**
     * 用户登录
     *
     * @return
     */
    List<User> selectAllUser();

    /**
     * 用户登录
     *
     * @param user
     * @return
     */
    User userLogin(User user);

    /**
     * 更新在线状态
     *
     * @param id
     * @param login
     * @return
     */
    void updateLogin(Integer id, Integer login);
}