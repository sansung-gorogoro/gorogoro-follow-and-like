package com.gorogoro.followandlike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GorogoroFollowAndLikeApplication {
    public static void main(String[] args) {
        SpringApplication.run(GorogoroFollowAndLikeApplication.class, args);
    }
}
