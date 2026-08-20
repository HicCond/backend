package com.example.quiz.dto;

import java.math.BigDecimal;
import java.util.List;

public record QuizResultDto(int totalScore,
							int maxScore,
							int correctCount,
							int incorrectCount,
							BigDecimal percentage,
							boolean passed,
							int passingScore,
							List<IncorrectAnswerDto> incorrectAnswers) {
}
