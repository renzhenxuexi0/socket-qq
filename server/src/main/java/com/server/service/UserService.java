package com.server.service;

import com.alibaba.fastjson.JSON;
import com.server.mapper.UserMapper;
import com.server.pojo.Data;
import com.server.pojo.User;
import com.server.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.concurrent.Callable;

public class UserService {
    // 创建数据库连接工厂
    private static final SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    private User user;

    public void setData(User user) {
        this.user = user;
    }

    public Callable<Boolean> userRegister() {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                    // 做数据校验 可以封装成工具类 如果有问题return false

                    // 如果没用问题
                    // 先把数据上传到数据库
                    // 1.得到mapper对象
                    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                    userMapper.addUser(user);
                    // 添加操作 需要提交事务
                    sqlSession.commit();
                    // 成功return true
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    // 出错返回false
                    return false;
                }
            }
        };
    }
}