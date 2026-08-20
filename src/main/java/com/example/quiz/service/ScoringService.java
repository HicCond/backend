package com.example.quiz.service;

import com.example.quiz.config.QuizProperties;
import com.example.quiz.dto.IncorrectAnswerDto;
import com.example.quiz.dto.QuizResultDto;
import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.dto.SubmittedAnswerDto;
import com.example.quiz.entity.QuestionEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoringService {

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
	private static final int PERCENTAGE_SCALE = 1;

	QuizProperties quizProperties;

	public QuizResultDto evaluate(List<QuestionEntity> questions, SubmitAnswersRequest request) {
		Map<String, String> chosenOptionByQuestion = request.answers().stream()
				.collect(Collectors.toMap(SubmittedAnswerDto::questionId, SubmittedAnswerDto::optionId));

		int totalScore = 0;
		int maxScore = 0;
		int correctCount = 0;
		List<IncorrectAnswerDto> incorrectAnswers = new ArrayList<>();

		for (QuestionEntity question : questions) {
			maxScore += question.getPoints();
			String chosenOptionId = chosenOptionByQuestion.get(question.getId());

			if (question.isCorrectOption(chosenOptionId)) {
				totalScore += question.getPoints();
				correctCount++;
			} else {
				incorrectAnswers.add(new IncorrectAnswerDto(
						question.getId(),
						question.getText(),
						question.getPoints(),
						chosenOptionId,
						question.optionText(chosenOptionId),
						question.getCorrectOptionId(),
						question.optionText(question.getCorrectOptionId())));
			}
		}

		return new QuizResultDto(
				totalScore,
				maxScore,
				correctCount,
				questions.size() - correctCount,
				percentageOfCorrectAnswers(correctCount, questions.size()),
				totalScore > quizProperties.passingScore(),
				quizProperties.passingScore(),
				List.copyOf(incorrectAnswers));
	}

	private BigDecimal percentageOfCorrectAnswers(int correctCount, int questionCount) {
		if (questionCount == 0) {
			return BigDecimal.ZERO.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
		}
		return BigDecimal.valueOf(correctCount)
				.multiply(HUNDRED)
				.divide(BigDecimal.valueOf(questionCount), PERCENTAGE_SCALE, RoundingMode.HALF_UP);
	}
}
