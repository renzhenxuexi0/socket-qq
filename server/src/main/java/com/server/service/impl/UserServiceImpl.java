package com.server.service.impl;

import com.server.mapper.UserMapper;
import com.server.pojo.User;
import com.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    // 自动注入得到mapper对象
    @Autowired
    private UserMapper userMapper;

    public boolean userRegister(User user) {
        // 做数据校验 可以封装成工具类 如果有问题return false
        // 如果没用问题
        // 先把数据上传到数据库
        // 1.得到mapper对象
        userMapper.addUser(user);
        return true;
    }

    public List<User> selectAllUser() {
        return userMapper.selectAll();
    }

    public User userLogin(User user) {
        // 获取输入的账号和密码
        return userMapper.selectByAccountAndPasswordUser(user);
    }

    public void updateIpAndPort(Integer id, String ip, Integer port) {
        userMapper.updateIpAndPort(id, ip, port);
    }


    public void updateLogin(Integer id, Integer login) {
        userMapper.updateLogin(id, login);
    }

}
