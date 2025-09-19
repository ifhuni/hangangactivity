package com.example.hangangactivity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.hangangactivity.mapper")
public class HangangactivityApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangangactivityApplication.class, args);
    }
}
