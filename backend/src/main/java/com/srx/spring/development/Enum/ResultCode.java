package com.srx.spring.development.Enum;

public enum ResultCode {
    LOGIN_SUCCESS("登录成功", 204),
    FILE_UPLOAD_SUCCESS("文件上传成功", 205),
    REQUIREMENTS_SUCCESS("需求库安装成功", 206),
    DATA_RETURN_SUCCESS("数据返回成功", 207),
    UPDATE_TASK_SUCCESS("任务更新成功", 208),
    QUEUE_TASK_SUCCESS("插入队列成功", 209),
    INSERT_ENV_SUCCESS("环境创建成功", 210),
    INIT_GPU_SUCCESS("gpu初始化成功", 211),
    UPDATE_PASSWORD_SUCCESS("密码更该成功", 213),
    REGISTER_SUCCESS("用户注册成功", 216),


    ENV_EXIT("环境已存在", 434),
    ENV_NOT_EXIT("环境不存在", 212),

    USERNAME_EXIT("该用户名已被占用", 437),
    USERNAME_NOT_EXIT("该用户名可以使用", 214),
    EMAIL_EXIT("该邮箱已注册", 438),
    EMAIL_NOT_EXIT("该邮箱可以使用", 215),
    TASK_NOT_EXIT("该任务不存在", 439),
    TASK_EXIT("该任务存在", 217),


    // 失败码
    ERROR_NOLOGIN("请先登录", 423),
    ERROR_NOFOUND_USER("密码或用户名错误，请先注册", 424),
    ERROR_NULL("请确保正确填写了表单", 425),
    ERROR_PARAM("传入参数有误", 427),
    ERROR_NO_MORE_DATA("已经没有数据了", 428),
    ERROR_NO_DATA("没有查询到数据", 429),
    FAIL_INSTALL_REQUIREMENTS("环境库安装失败", 426),
    FAIL_UPDATE_TASK("任务更新失败", 430),
    FAIL_QUEUE_TASK("插入队列失败", 431),
    FAIL_INSERT_ENV("环境创建失败", 432),
    FAIL_INIT_GPU("gpu初始化失败", 433),
    FAIL_UPDATE_PASSWORD("更改密码失败", 435),
    FAIL_REGISTER("注册失败", 436),

    ;

    private final String message;
    private final Integer code;

    ResultCode(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "ResultCode{" +
                "message='" + message + '\'' +
                ", code=" + code +
                '}';
    }
}
