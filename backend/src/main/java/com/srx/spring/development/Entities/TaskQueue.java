package com.srx.spring.development.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskQueue {
    private String taskKey;
    private String gpuId;
    private String joinTime;
    private String status;
}
