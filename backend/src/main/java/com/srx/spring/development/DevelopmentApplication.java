package com.srx.spring.development;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.srx.spring.development.Mapper")
@EnableScheduling
public class DevelopmentApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevelopmentApplication.class, args);
    }

}

