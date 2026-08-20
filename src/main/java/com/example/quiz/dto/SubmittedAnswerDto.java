package com.example.quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmittedAnswerDto(@NotBlank(message = "questionId must not be blank") String questionId,
								 @NotBlank(message = "optionId must not be blank") String optionId) {
}
