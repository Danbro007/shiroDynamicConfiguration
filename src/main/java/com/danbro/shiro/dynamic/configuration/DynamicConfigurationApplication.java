package com.danbro.shiro.dynamic.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.danbro.shiro.dynamic.configuration.mapper")
public class DynamicConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicConfigurationApplication.class, args);
    }

}
