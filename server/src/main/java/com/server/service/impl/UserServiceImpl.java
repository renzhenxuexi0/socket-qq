package com.server.service.impl;

import com.server.mapper.UserMapper;
import com.server.pojo.User;
import com.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

@Service
public class UserServiceImpl implements UserService {
    // 自动注入得到mapper对象
    @Autowired
    private UserMapper userMapper;

    public Callable<Boolean> userRegister(User user) {
        return () -> {
            // 做数据校验 可以封装成工具类 如果有问题return false
            // 如果没用问题
            // 先把数据上传到数据库
            // 1.得到mapper对象
            userMapper.addUser(user);
            // 成功return true
            return true;
        };
    }

    public Callable<List<User>> selectAllUser() {
        return () -> {
            // 先把数据上传到数据库
            return userMapper.selectAll();
        };
    }

    public Callable<User> userLogin(User user) {
        return () -> {
            // 获取输入的账号和密码
            return userMapper.selectByAccountAndPasswordUser(user);
        };
    }

    public Runnable updateLogin(Integer id, Integer login) {
        return () -> userMapper.updateLogin(id, login);
    }
}
