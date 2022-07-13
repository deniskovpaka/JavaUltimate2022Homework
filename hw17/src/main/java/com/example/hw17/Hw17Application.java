package com.example.hw17;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class Hw17Application {

    public static void main(String[] args) {
        SpringApplication.run(Hw17Application.class, args);
    }

}
