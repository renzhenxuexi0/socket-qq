package com.server.service;

import com.server.mapper.UserMapper;
import com.server.pojo.User;
import com.server.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class UserService {
    // 创建数据库连接工厂
    private static final SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    public Callable<Boolean> userRegister(User user) {
        return new Callable<>() {
            @Override
            public Boolean call() {
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


    public Callable<List<User>> selectAllUser() {
        return () -> {
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                // 做数据校验 可以封装成工具类 如果有问题return false

                // 如果没用问题
                // 先把数据上传到数据库
                // 1.得到mapper对象
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                return userMapper.selectAll();
            } catch (Exception e) {
                e.printStackTrace();
                // 出错返回null
                return null;
            }
        };
    }

    public Callable<User> userLogin(User user) {
        return new Callable<>() {
            public User call() {
                try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                    // 得到mapper类
                    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                    // 获取输入的账号和密码
                    return userMapper.selectByAccountNumberAndPasswordUser(user);

                } catch (Exception e) {
                    e.printStackTrace();
                    // 出错返回null
                    return null;
                }
            }

        };

    }
}