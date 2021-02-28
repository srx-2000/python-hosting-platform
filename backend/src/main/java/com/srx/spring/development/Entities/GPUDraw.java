package com.srx.spring.development.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPUDraw {
    private String id;
    private String name;
    private String gpuId;
    private String usageRate;
    private String createTime;
}
