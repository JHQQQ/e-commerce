package com.mitsuha754.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@MapperScan("com.mitsuha754.ecommerce.mapper")

public class ECommerceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ECommerceServerApplication.class, args);
    }
}
