package com.example.quiz.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public record AnswerOption(String id, String text) {
}
