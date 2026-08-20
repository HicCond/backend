package com.example.quiz;

import com.example.quiz.config.QuizProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(QuizProperties.class)
public class QuizApplication {

    static void main(String[] args) {
        SpringApplication.run(QuizApplication.class, args);
    }
}
