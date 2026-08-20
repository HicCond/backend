package com.example.quiz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quiz")
public record QuizProperties(String title, String description, int passingScore) {
}
