package com.example.quiz.dto;

import java.util.List;

public record QuestionDto(String id, String text, int points, List<OptionDto> options) {
}
