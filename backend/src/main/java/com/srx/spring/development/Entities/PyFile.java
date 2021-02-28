package com.srx.spring.development.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public class PyFile implements Serializable {
    private Integer pyId;
    private String taskKey;
    private String filePath;
    private String fileName;
    private String status;
}
