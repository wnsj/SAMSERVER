package com.jiubo.sam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//开启事务管理
@EnableTransactionManagement
//扫描mapper
@MapperScan("com.jiubo.sam.dao")
public class SamApplication {

    public static void main(String[] args) {
        SpringApplication.run(SamApplication.class, args);
    }
}
