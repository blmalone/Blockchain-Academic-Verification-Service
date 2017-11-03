package com.unilog.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.unilog")
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
public class UnilogServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnilogServiceApplication.class, args);
    }
}

