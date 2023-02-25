package com.sparta.blushmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BlushMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlushMarketApplication.class, args);
    }

}
