package com.example.quiz.service;

import com.example.quiz.config.QuizProperties;
import com.example.quiz.dto.QuestionDto;
import com.example.quiz.dto.QuizResultDto;
import com.example.quiz.dto.QuizRulesDto;
import com.example.quiz.dto.ScoringRuleDto;
import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.entity.QuestionEntity;
import com.example.quiz.mapper.QuestionMapper;
import com.example.quiz.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {

	QuestionRepository questionRepository;
	QuestionMapper questionMapper;
	AnswerValidator answerValidator;
	ScoringService scoringService;
	QuizProperties quizProperties;

	public QuizRulesDto getRules() {
		List<QuestionEntity> questions = questionRepository.findAllByOrderByIdAsc();
		return new QuizRulesDto(
				quizProperties.title(),
				quizProperties.description(),
				questions.size(),
				questions.stream().mapToInt(QuestionEntity::getPoints).sum(),
				quizProperties.passingScore(),
				scoringBreakdown(questions));
	}

	public List<QuestionDto> getQuestions() {
		return questionMapper.toDtoList(questionRepository.findAllByOrderByIdAsc());
	}

	public QuizResultDto submit(SubmitAnswersRequest request) {
		List<QuestionEntity> questions = questionRepository.findAllByOrderByIdAsc();
		answerValidator.validate(questions, request);
		QuizResultDto result = scoringService.evaluate(questions, request);
		log.info("Submission evaluated: score={}/{}, correct={}, incorrect={}, passed={}",
				result.totalScore(), result.maxScore(), result.correctCount(), result.incorrectCount(),
				result.passed());
		return result;
	}

	private List<ScoringRuleDto> scoringBreakdown(List<QuestionEntity> questions) {
		return questions.stream()
				.collect(Collectors.groupingBy(QuestionEntity::getPoints, Collectors.counting()))
				.entrySet().stream()
				.sorted(Map.Entry.<Integer, Long>comparingByKey(Comparator.reverseOrder()))
				.map(tier -> new ScoringRuleDto(
						tier.getKey(),
						tier.getValue().intValue(),
						tier.getKey() * tier.getValue().intValue()))
				.toList();
	}
}
