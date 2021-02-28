package com.srx.spring.development.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPUInfo {
    private Integer number;

    private String name;

    private String totalMemory;

    private String usedMemory;

    private String usableMemory;

    private Double usageRate;

    private String status;

}

