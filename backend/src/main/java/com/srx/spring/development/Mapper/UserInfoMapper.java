package com.srx.spring.development.Mapper;

import com.srx.spring.development.Entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoMapper {

    /**
     * 插入用户信息,与插入用户基本信息组成一个事务
     * @param user
     * @return
     */
    Boolean insertUserInfo(User user);



}
