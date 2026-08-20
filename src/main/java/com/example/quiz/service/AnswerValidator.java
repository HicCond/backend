package com.example.quiz.service;

import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.dto.SubmittedAnswerDto;
import com.example.quiz.dto.ValidationErrorDto;
import com.example.quiz.entity.QuestionEntity;
import com.example.quiz.exception.InvalidSubmissionException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class AnswerValidator {

	public void validate(List<QuestionEntity> questions, SubmitAnswersRequest request) {
		Map<String, QuestionEntity> questionsById = questions.stream()
				.collect(Collectors.toMap(QuestionEntity::getId, Function.identity()));
		List<SubmittedAnswerDto> answers = request.answers();

		List<ValidationErrorDto> errors = Stream.of(
						answerViolations(questionsById, answers),
						duplicateViolations(answers),
						missingAnswerViolations(questions, answers))
				.flatMap(Function.identity())
				.toList();

		if (!errors.isEmpty()) {
			throw new InvalidSubmissionException(errors);
		}
	}

	private Stream<ValidationErrorDto> answerViolations(Map<String, QuestionEntity> questionsById,
														List<SubmittedAnswerDto> answers) {
		return answers.stream()
				.map(answer -> violationOf(questionsById.get(answer.questionId()), answer))
				.flatMap(Optional::stream);
	}

	private Optional<ValidationErrorDto> violationOf(QuestionEntity question, SubmittedAnswerDto answer) {
		if (question == null) {
			return Optional.of(ValidationErrorDto.unknownQuestion(answer.questionId()));
		}
		if (!question.offersOption(answer.optionId())) {
			return Optional.of(ValidationErrorDto.optionNotInQuestion(answer.questionId(), answer.optionId()));
		}
		return Optional.empty();
	}

	private Stream<ValidationErrorDto> duplicateViolations(List<SubmittedAnswerDto> answers) {
		return answers.stream()
				.collect(Collectors.groupingBy(SubmittedAnswerDto::questionId, LinkedHashMap::new,
						Collectors.counting()))
				.entrySet().stream()
				.filter(answered -> answered.getValue() > 1)
				.map(answered -> ValidationErrorDto.duplicateAnswer(answered.getKey()));
	}

	private Stream<ValidationErrorDto> missingAnswerViolations(List<QuestionEntity> questions,
															   List<SubmittedAnswerDto> answers) {
		Set<String> answeredQuestionIds = answers.stream()
				.map(SubmittedAnswerDto::questionId)
				.collect(Collectors.toSet());
		return questions.stream()
				.map(QuestionEntity::getId)
				.filter(questionId -> !answeredQuestionIds.contains(questionId))
				.map(ValidationErrorDto::missingAnswer);
	}
}
