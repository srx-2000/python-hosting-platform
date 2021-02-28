package com.srx.spring.development.Service;

import com.srx.spring.development.Entities.User;

public interface UserService {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    User login(String username,String password);

    /**
     * 注册
     * @param user
     * @return
     */
    Boolean register(User user);

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    Boolean isUsernameExist(String username);

    /**
     * 检查邮箱是否已注册
     * @param email
     * @return
     */
    Boolean isEmailExist(String email);

    /**
     * 修改密码
     * @param email
     * @param oldPassword
     * @param newPassword
     * @return
     */
    Boolean updatePassword(String email,String oldPassword,String newPassword);
}
