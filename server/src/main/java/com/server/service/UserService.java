package com.server.service;

import com.server.mapper.UserMapper;
import com.server.pojo.Code;
import com.server.pojo.User;
import com.server.utils.SqlSessionFactoryUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

public class UserService {
    // 创建数据库连接工厂
    private static final SqlSessionFactory sqlSessionFactory = SqlSessionFactoryUtils.getSqlSessionFactory();

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public Callable<Boolean> userRegister() {
        return new Callable<Boolean>() {
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
    public Callable<Boolean> userLogin(){
        return new Callable<Boolean>(){
            public Boolean call(){
                try(SqlSession sqlSession = sqlSessionFactory.openSession()){
                    //得到mapper类
                    UserMapper userMapper= sqlSession.getMapper(UserMapper.class);
                    //获取输入的账号和密码
                    User user2 = userMapper.selectbyAccountNumberAndPasswordUser(user);
                    return null != user2;

                }catch (Exception e){
                    e.printStackTrace();
                    // 出错返回false
                    return false;
                 }
            }

        };

    }
}