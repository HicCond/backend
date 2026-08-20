package com.example.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubmitAnswersRequest(@NotEmpty(message = "answers must not be empty")
								   @Valid List<SubmittedAnswerDto> answers) {
}
