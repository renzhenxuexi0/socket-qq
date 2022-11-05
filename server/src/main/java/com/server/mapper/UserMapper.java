package com.server.mapper;

import com.server.pojo.User;

import java.util.List;

public interface UserMapper {

    /**
     * 添加用户
     * @param user
     */
    void addUser(User user);

    /**
     * 查询所有用户
     */
    List<User> selectAll();


    User selectbyAccountNumberAndPasswordUser(User user);
}
