package com.example.quiz.dto;

import java.util.List;

public record QuizRulesDto(String title,
						   String description,
						   int questionCount,
						   int maxScore,
						   int passingScore,
						   List<ScoringRuleDto> scoring) {
}
