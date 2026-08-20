package com.example.quiz.service;

import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.dto.SubmittedAnswerDto;
import com.example.quiz.dto.ValidationErrorDto;
import com.example.quiz.entity.AnswerOption;
import com.example.quiz.entity.QuestionEntity;
import com.example.quiz.exception.InvalidSubmissionException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.quiz.dto.ValidationErrorCode.DUPLICATE_ANSWER;
import static com.example.quiz.dto.ValidationErrorCode.MISSING_ANSWER;
import static com.example.quiz.dto.ValidationErrorCode.OPTION_NOT_IN_QUESTION;
import static com.example.quiz.dto.ValidationErrorCode.UNKNOWN_QUESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

class AnswerValidatorTest {

	private static final List<QuestionEntity> QUESTIONS = List.of(
			QuestionEntity.builder()
					.id("q01")
					.text("First question")
					.points(100)
					.correctOptionId("a")
					.options(List.of(new AnswerOption("a", "Right"), new AnswerOption("b", "Wrong")))
					.build(),
			QuestionEntity.builder()
					.id("q02")
					.text("Second question")
					.points(75)
					.correctOptionId("c")
					.options(List.of(new AnswerOption("a", "Wrong"), new AnswerOption("b", "Wrong"),
							new AnswerOption("c", "Right")))
					.build());

	private final AnswerValidator answerValidator = new AnswerValidator();

	@Test
	void acceptsACompleteSubmissionWithValidOptions() {
		SubmitAnswersRequest request = submission(answer("q01", "a"), answer("q02", "c"));

		assertThatCode(() -> answerValidator.validate(QUESTIONS, request)).doesNotThrowAnyException();
	}

	@Test
	void rejectsAnOptionThatBelongsToAnotherQuestion() {
		SubmitAnswersRequest request = submission(answer("q01", "c"), answer("q02", "c"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code, ValidationErrorDto::questionId)
						.containsExactly(tuple(OPTION_NOT_IN_QUESTION, "q01")));
	}

	@Test
	void rejectsAnOptionThatDoesNotExistAtAll() {
		SubmitAnswersRequest request = submission(answer("q01", "z"), answer("q02", "c"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code)
						.containsExactly(OPTION_NOT_IN_QUESTION));
	}

	@Test
	void rejectsAnAnswerForAQuestionOutsideThisQuiz() {
		SubmitAnswersRequest request = submission(answer("q01", "a"), answer("q02", "c"), answer("q99", "a"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code, ValidationErrorDto::questionId)
						.containsExactly(tuple(UNKNOWN_QUESTION, "q99")));
	}

	@Test
	void rejectsTheSameQuestionAnsweredTwice() {
		SubmitAnswersRequest request = submission(answer("q01", "a"), answer("q01", "b"), answer("q02", "c"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code, ValidationErrorDto::questionId)
						.containsExactly(tuple(DUPLICATE_ANSWER, "q01")));
	}

	@Test
	void rejectsASubmissionThatLeavesAQuestionUnanswered() {
		SubmitAnswersRequest request = submission(answer("q01", "a"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code, ValidationErrorDto::questionId)
						.containsExactly(tuple(MISSING_ANSWER, "q02")));
	}

	@Test
	void reportsEveryViolationInASingleResponse() {
		SubmitAnswersRequest request = submission(answer("q99", "a"), answer("q01", "z"));

		assertThatExceptionOfType(InvalidSubmissionException.class)
				.isThrownBy(() -> answerValidator.validate(QUESTIONS, request))
				.satisfies(exception -> assertThat(exception.getErrors())
						.extracting(ValidationErrorDto::code, ValidationErrorDto::questionId)
						.containsExactly(
								tuple(UNKNOWN_QUESTION, "q99"),
								tuple(OPTION_NOT_IN_QUESTION, "q01"),
								tuple(MISSING_ANSWER, "q02")));
	}

	private static SubmitAnswersRequest submission(SubmittedAnswerDto... answers) {
		return new SubmitAnswersRequest(List.of(answers));
	}

	private static SubmittedAnswerDto answer(String questionId, String optionId) {
		return new SubmittedAnswerDto(questionId, optionId);
	}
}
