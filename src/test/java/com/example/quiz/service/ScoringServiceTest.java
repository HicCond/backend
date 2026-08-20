package com.example.quiz.service;

import com.example.quiz.QuizFixtures;
import com.example.quiz.config.QuizProperties;
import com.example.quiz.dto.QuizResultDto;
import com.example.quiz.entity.QuestionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoringServiceTest {

	private static final int PASSING_SCORE = 600;

	private final ScoringService scoringService =
			new ScoringService(new QuizProperties("World Geography", "Test quiz", PASSING_SCORE));

	@Test
	void awardsEveryPointWhenAllAnswersAreCorrect() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 20));

		assertThat(result.totalScore()).isEqualTo(1000);
		assertThat(result.maxScore()).isEqualTo(1000);
		assertThat(result.correctCount()).isEqualTo(20);
		assertThat(result.incorrectCount()).isZero();
		assertThat(result.percentage()).isEqualByComparingTo("100.0");
		assertThat(result.passed()).isTrue();
		assertThat(result.incorrectAnswers()).isEmpty();
	}

	@Test
	void awardsNoPointsWhenAllAnswersAreWrong() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 0));

		assertThat(result.totalScore()).isZero();
		assertThat(result.correctCount()).isZero();
		assertThat(result.incorrectCount()).isEqualTo(20);
		assertThat(result.percentage()).isEqualByComparingTo("0.0");
		assertThat(result.passed()).isFalse();
		assertThat(result.incorrectAnswers()).hasSize(20);
	}

	@Test
	@DisplayName("600 points is a failure: the quiz is passed only when the score exceeds 600")
	void failsWhenScoreIsExactlyAtTheThreshold() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 7));

		assertThat(result.totalScore()).isEqualTo(PASSING_SCORE);
		assertThat(result.passed()).isFalse();
	}

	@Test
	void passesWhenScoreIsOnePointTierAboveTheThreshold() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 8));

		assertThat(result.totalScore()).isEqualTo(640);
		assertThat(result.passed()).isTrue();
	}

	@Test
	void derivesPercentageFromTheNumberOfQuestionsNotFromPoints() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 14));

		assertThat(result.correctCount()).isEqualTo(14);
		assertThat(result.totalScore()).isEqualTo(850);
		assertThat(result.percentage()).isEqualByComparingTo("70.0");
	}

	@Test
	void reportsEachIncorrectAnswerWithItsQuestionTextAndCorrectOption() {
		List<QuestionEntity> questions = QuizFixtures.twentyQuestions();

		QuizResultDto result = scoringService.evaluate(questions, QuizFixtures.answers(questions, 19));

		assertThat(result.incorrectAnswers()).singleElement().satisfies(incorrect -> {
			assertThat(incorrect.questionId()).isEqualTo("q20");
			assertThat(incorrect.questionText()).isEqualTo("Question q20");
			assertThat(incorrect.points()).isEqualTo(25);
			assertThat(incorrect.givenOptionId()).isEqualTo(QuizFixtures.WRONG_OPTION);
			assertThat(incorrect.givenOptionText()).isEqualTo("Wrong answer");
			assertThat(incorrect.correctOptionId()).isEqualTo(QuizFixtures.CORRECT_OPTION);
			assertThat(incorrect.correctOptionText()).isEqualTo("Correct answer");
		});
	}
}
