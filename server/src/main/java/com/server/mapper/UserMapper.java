package com.server.mapper;

import com.server.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 添加用户
     *
     * @param user
     */
    void addUser(User user);

    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 根据账户和密码查询信息
     *
     * @param user
     * @return
     */
    User selectByAccountAndPasswordUser(User user);

    /**
     * 更新登录状态
     *
     * @param id
     * @param login
     */
    void updateLoginById(@Param("id") Integer id, @Param("login") Integer login);

    /**
     * 更新ip地址
     *
     * @param id
     * @param ip
     */
    void updateIpById(@Param("id") Integer id, @Param("ip") String ip);
}
