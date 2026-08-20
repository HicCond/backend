package com.example.quiz.dto;

public record IncorrectAnswerDto(String questionId,
								 String questionText,
								 int points,
								 String givenOptionId,
								 String givenOptionText,
								 String correctOptionId,
								 String correctOptionText) {
}
