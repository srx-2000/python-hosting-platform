package com.srx.spring.development.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OutputFile implements Serializable {
    private Integer outputId;
    private String taskKey;
    private String filePath;
    private String fileName;
    private String status;
}
