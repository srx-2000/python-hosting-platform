package com.srx.spring.development.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;
@Component
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private String userKey;
    private String nickname;
    private String phone;

    public User(Integer userId, String username, String email, String password, String nickname, String phone) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phone = phone;
    }

}
