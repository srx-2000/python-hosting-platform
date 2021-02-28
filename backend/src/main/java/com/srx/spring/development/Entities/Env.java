package com.srx.spring.development.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Env implements Serializable {
    private Integer envId;
    private String status;
    private String createTime;
    private String hasList;
    private String finalLiveTime;
    private String envName;
    private String userKey;
    private List<Task> taskList;

}
