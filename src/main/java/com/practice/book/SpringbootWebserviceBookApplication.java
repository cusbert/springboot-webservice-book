package com.practice.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringbootWebserviceBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootWebserviceBookApplication.class, args);
    }

}
