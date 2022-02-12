
DROP DATABASE IF EXISTS development;

CREATE DATABASE development;

USE development;

CREATE TABLE `user`(
                       user_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "用户id",
                       username VARCHAR(45) NOT NULL UNIQUE COMMENT "用户名",
                       email VARCHAR(45) NOT NULL UNIQUE COMMENT "邮箱",
                       `password` VARCHAR(45) NOT NULL COMMENT "密码",
                       user_key VARCHAR(36) NOT NULL UNIQUE COMMENT "服务器使用uuid生成32位用户秘钥"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户基础信息表';

CREATE TABLE user_info(
                          user_id INT NOT NULL,
                          nickname VARCHAR(255) NOT NULL COMMENT '用户昵称',
                          phone VARCHAR(11) NOT NULL COMMENT "用户电话",
                          FOREIGN KEY(user_id) REFERENCES `user`(user_id)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

CREATE TABLE task(
                     task_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "任务id",
                     task_title VARCHAR(255) NOT NULL COMMENT "任务名称",
                     create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
                     task_key VARCHAR(36) NOT NULL UNIQUE COMMENT "任务秘钥",
                     user_key VARCHAR(36) NOT NULL COMMENT "用户秘钥",
                     dead_time INT NOT NULL DEFAULT 3600 COMMENT "用户规定的最大存活时间，默认1小时3600秒，单位秒",
                     final_live_time INT NOT NULL DEFAULT 0 COMMENT "在任务完成的时候记录环境存活时间",
                     `status` TINYINT DEFAULT 1 NOT NULL COMMENT "1表示未开始,2表示已加入队列，0表示正在运行，-1表示已经完成",
                     task_description BLOB COMMENT "任务描述",
                     requirements_path VARCHAR(255) COMMENT "requirements.txt文件的路径，用来存储所需要的的库",
                     gpu_id INT NOT NULL COMMENT "gpuId，用来标注每个任务到底使用哪个gpu的"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='任务信息表';

CREATE TABLE env(
                    env_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "环境id",
                    `status` TINYINT NOT NULL DEFAULT 1 COMMENT "环境状态，默认为1表示创建了，0表示销毁了",
                    has_list BLOB NOT NULL COMMENT "该环境下已存在的所有的库，可通过pip list查询，并存放在一个list.txt文件下，在数据库中存储该文件的路径",
                    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '环境创建时间',
                    final_live_time INT NOT NULL DEFAULT 0 COMMENT "在环境销毁的时候记录环境存活时间",
                    env_name VARCHAR(255) NOT NULL UNIQUE COMMENT "环境名称,每个用户不可以创建重名环境，会被覆盖，通过在名称后拼接任务秘钥 实现唯一"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='环境表';

CREATE TABLE input(
                      input_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "输入id",
                      task_key VARCHAR(36) NOT NULL COMMENT "这里之所以只使用task_key，是因为task_key已经足够确保每个输入对应每个任务的唯一路径，不需要再用user_key以及env_name进行约束",
                      file_path VARCHAR(255) NOT NULL COMMENT "相应的py文件的存储路径，需要用户在编码时确保其正确性，形似：{/user_key/env_name/task_key/input/文件}",
                      `status` TINYINT NOT NULL DEFAULT 1 COMMENT "1表示存在，0表示已删除",
                      file_name VARCHAR(255) NOT NULL COMMENT "文件名"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='输入表';

CREATE TABLE output(
                       output_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "输出id",
                       task_key VARCHAR(36) NOT NULL COMMENT "这里之所以只使用task_key，是因为task_key已经足够确保每个输入对应每个任务的唯一路径，不需要再用user_key以及env_name进行约束",
                       file_path VARCHAR(255) NOT NULL COMMENT "相应的py文件的存储路径，需要用户在编码时确保其正确性，形似：{/user_key/env_name/task_key/output/文件}",
                       `status` TINYINT NOT NULL DEFAULT 1 COMMENT "1表示存在，0表示已删除",
                       file_name VARCHAR(255) NOT NULL COMMENT "文件名"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='输出表';


CREATE TABLE py(
                   py_id INT PRIMARY KEY AUTO_INCREMENT COMMENT "py文件id",
                   task_key VARCHAR(36) NOT NULL COMMENT "这里之所以只使用task_key，是因为task_key已经足够确保每个输入对应每个任务的唯一路径，不需要再用user_key以及env_name进行约束",
                   file_path VARCHAR(255) NOT NULL COMMENT "相应的py文件的存储路径，需要用户在编码时确保其正确性，形似：{/user_key/env_name/task_key/py/文件}",
                   `status` TINYINT NOT NULL DEFAULT 1 COMMENT "1表示存在，0表示已删除",
                   file_name VARCHAR(255) NOT NULL COMMENT "文件名"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='py文件表';


CREATE TABLE env_task(
                         env_id INT NOT NULL COMMENT "环境id",
                         task_id INT NOT NULL COMMENT "任务id",
                         task_key VARCHAR(36) NOT NULL UNIQUE COMMENT "任务秘钥",
                         user_key VARCHAR(36) NOT NULL COMMENT "用户秘钥"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='环境与任务映射表';


CREATE TABLE gpu(
                    number INT PRIMARY KEY COMMENT "gpu编号",
                    `name` VARCHAR(255) NOT NULL COMMENT "名字",
                    total_memory VARCHAR(255) NOT NULL COMMENT "总内存",
                    used_memory VARCHAR(255) NOT NULL COMMENT "已用内存",
                    usable_memory VARCHAR(255) NOT NULL COMMENT "可用内存",
                    usage_rate DOUBLE(5,2) NOT NULL COMMENT "利用率",
                    `status` TINYINT NOT NULL DEFAULT 0 COMMENT "gpu状态，0表示未使用，-1表示已删除，1表示正在使用"
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='gpu表';

CREATE TABLE task_queue(
                           task_key VARCHAR(36) NOT NULL COMMENT "任务key",
                           gpu_id INT NOT NULL COMMENT "gpu号，用来确定task在哪个队列里的",
                           join_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT "任务加入队列时间",
                           `status` TINYINT NOT NULL DEFAULT 1 COMMENT "任务在队列里的状态，1表示在队列中，0表示已经从队列中弹出了",
                           FOREIGN KEY(task_key) REFERENCES task(task_key)
)ENGINE=INNOBASE DEFAULT CHARSET=utf8 COMMENT="任务队列表，该表主要是用来缓存队列的，因为在taskservice中的任务队列无法做到全局暴露，同时内存中的队列的安全也没有保证";

CREATE TABLE gpu_draw(
                         id INT PRIMARY KEY AUTO_INCREMENT COMMENT "主键",
                         `name` VARCHAR(255) NOT NULL COMMENT "gpu名字",
                         gpu_id INT COMMENT "gpu编号",
                         usage_rate DOUBLE(5,2) NOT NULL COMMENT "利用率",
                         create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'gpu状态入库时间'
)ENGINE=INNOBASE DEFAULT CHARSET=utf8 COMMENT="用来给gpu画图，每隔一小时监控一次gpu状态";

CREATE TABLE user_login(
                           user_key VARCHAR(36) NOT NULL COMMENT "用户秘钥",
                           login_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT "用户登录时间",
                           login_country VARCHAR(255) NOT NULL COMMENT "用户登录国家",
                           login_region VARCHAR(255) NOT NULL COMMENT "用户登录地区",
                           login_city VARCHAR(255) NOT NULL COMMENT "用户登录城市",
                           login_isp VARCHAR(255) NOT NULL COMMENT "用户登录使用的ip的代理商",
                           FOREIGN KEY(user_key) REFERENCES `user`(user_key)
)ENGINE=INNOBASE DEFAULT CHARSET=utf8 COMMENT="用户登录信息表，在每次用户登录的时候插入一条数据";

