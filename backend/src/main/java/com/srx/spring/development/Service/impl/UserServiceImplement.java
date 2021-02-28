package com.srx.spring.development.Service.impl;


import com.srx.spring.development.Entities.User;
import com.srx.spring.development.Mapper.UserInfoMapper;
import com.srx.spring.development.Mapper.UserMapper;
import com.srx.spring.development.Service.UserService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import com.srx.spring.development.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImplement implements UserService {

    private PropertiesLoader loader = new PropertiesLoader("config.properties");
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public User login(String username, String password) {
        if (username != null && password != null)
            return userMapper.login(username, CodeUtil.get_MD5_code(password));
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean register(User user) {
        if (user != null) {
            user.setUserKey(CodeUtil.get_uuid());
            String password = user.getPassword();
            user.setPassword(CodeUtil.get_MD5_code(password));
            boolean flag;
            if (CommandUtil.isWindows()) {
                flag = CommandUtil.mkdir(user.getUserKey(), null, null, CommandUtil.MKDIR_USER, CommandUtil.WINDOWS);
            } else {
                flag = CommandUtil.mkdir(user.getUserKey(), null, null, CommandUtil.MKDIR_USER, CommandUtil.LINUX);
            }
            if (flag) {
                Boolean result = userMapper.insertUser(user);
                Boolean result1 = userInfoMapper.insertUserInfo(user);
                return result && result1;
            }
        }
        return false;
    }

    @Override
    public Boolean isUsernameExist(String username) {
        String s = userMapper.queryUsername(username);
        if (s != null)
            return true;
        else
            return false;
    }

    @Override
    public Boolean isEmailExist(String email) {
        String s = userMapper.queryEmail(email);
        if (s != null)
            return true;
        else
            return false;
    }

    @Override
    public Boolean updatePassword(String email, String oldPassword, String newPassword) {
        User login = userMapper.login(email, CodeUtil.get_MD5_code(oldPassword));
        if (login != null) {
            Boolean aBoolean = userMapper.updatePassword(email, CodeUtil.get_MD5_code(newPassword));
            return aBoolean;
        } else return false;
    }


}
