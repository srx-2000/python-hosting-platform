package com.srx.spring.development.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {
    private Integer taskId;
    private String taskTitle;
    private String createTime;
    private String taskKey;
    private String userKey;
    private String status;
    private String taskDescription;
    private String requirementsPath;
    private String finalLiveTime;
    private String deadTime;
    private String gpuId;
    private Env env;
}
