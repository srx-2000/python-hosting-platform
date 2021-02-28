
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
