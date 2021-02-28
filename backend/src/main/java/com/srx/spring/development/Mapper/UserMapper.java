package com.srx.spring.development.Mapper;

import com.srx.spring.development.Entities.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserMapper {
    /**
     * 登录方法，成功就返回对象实体
     * @param username
     * @param password
     * @return
     */
    User login(@Param("username") String username,@Param("password") String password);

    /**
     * 插入一个用户，用来注册，传入的用户必须已经带有uuid
     * @param user
     * @return
     */
    Boolean insertUser(User user);

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    String queryUsername(@Param("username") String username);

    /**
     * 检查邮箱是否已注册
     * @param email
     * @return
     */
    String queryEmail(@Param("email") String email);

    /**
     * 修改密码
     * @param email
     * @param password
     * @return
     */
    Boolean updatePassword(@Param("email") String email,@Param("password") String password);



}
