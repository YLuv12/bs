package com.bs.tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.bs.tourism.mapper")
public class BsTourismApplication {
    public static void main(String[] args) {
        SpringApplication.run(BsTourismApplication.class, args);
    }
}