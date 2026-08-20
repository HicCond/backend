package com.example.quiz.controller;

import com.example.quiz.dto.QuestionDto;
import com.example.quiz.dto.QuizRulesDto;
import com.example.quiz.service.QuizService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {

	QuizService quizService;

	@GetMapping
	public QuizRulesDto getRules() {
		return quizService.getRules();
	}

	@GetMapping("/questions")
	public List<QuestionDto> getQuestions() {
		return quizService.getQuestions();
	}
}
