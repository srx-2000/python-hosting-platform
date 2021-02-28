package com.srx.spring.development.Controller;

import com.srx.spring.development.Entities.DTO.ResultMessage;
import com.srx.spring.development.Entities.User;
import com.srx.spring.development.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.srx.spring.development.Enum.ResultCode.*;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public ResultMessage login(@RequestBody User user, HttpSession session) {
        System.out.println(user);
        if (user != null) {
            String username = user.getUsername();
            String password = user.getPassword();
            if (username != null && password != null) {
                User login = userService.login(username, password);
                if (login != null) {
                    session.setAttribute("user", login);
                    return new ResultMessage(LOGIN_SUCCESS, login);
                } else {
                    return new ResultMessage(ERROR_NOFOUND_USER, null);
                }
            }
            return new ResultMessage(ERROR_NULL, null);
        }
        return new ResultMessage(ERROR_NULL, null);
    }

    @PostMapping("/register")
    public ResultMessage register(@RequestBody User user) {
        if (user != null) {
            Boolean register = userService.register(user);
            if (register)
                return new ResultMessage(REGISTER_SUCCESS, register);
            else return new ResultMessage(FAIL_REGISTER, register);
        } else return new ResultMessage(FAIL_REGISTER, false);
    }

    @PostMapping("/updatePassword")
    public ResultMessage updatePassword(@RequestParam String email,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        Boolean flag = userService.updatePassword(email, oldPassword, newPassword);
        if (flag) {
            return new ResultMessage(UPDATE_PASSWORD_SUCCESS, flag);
        } else {
            return new ResultMessage(FAIL_UPDATE_PASSWORD, flag);
        }
    }

    @GetMapping("/isUsernameExist")
    public ResultMessage isUsernameExist(@RequestParam String username) {
        Boolean flag = userService.isUsernameExist(username);
        if (flag) {
            return new ResultMessage(USERNAME_EXIT, flag);
        } else {
            return new ResultMessage(USERNAME_NOT_EXIT, flag);
        }
    }

    @GetMapping("/isEmailExist")
    public ResultMessage isEmailExist(@RequestParam String email) {
        Boolean flag = userService.isEmailExist(email);
        if (flag) {
            return new ResultMessage(EMAIL_EXIT, flag);
        } else {
            return new ResultMessage(EMAIL_NOT_EXIT, flag);
        }
    }

}
