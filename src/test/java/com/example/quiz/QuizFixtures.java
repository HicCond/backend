package com.example.quiz;

import com.example.quiz.dto.SubmitAnswersRequest;
import com.example.quiz.dto.SubmittedAnswerDto;
import com.example.quiz.entity.AnswerOption;
import com.example.quiz.entity.QuestionEntity;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class QuizFixtures {

	public static final String CORRECT_OPTION = "a";
	public static final String WRONG_OPTION = "b";

	private static final int[] POINTS_PER_QUESTION = {
			100, 100, 100,
			75, 75, 75, 75,
			40, 40, 40, 40, 40,
			25, 25, 25, 25, 25, 25, 25, 25
	};

	public static List<QuestionEntity> twentyQuestions() {
		List<QuestionEntity> questions = new ArrayList<>();
		for (int index = 0; index < POINTS_PER_QUESTION.length; index++) {
			questions.add(question("q%02d".formatted(index + 1), POINTS_PER_QUESTION[index]));
		}
		return List.copyOf(questions);
	}

	public static QuestionEntity question(String id, int points) {
		return QuestionEntity.builder()
				.id(id)
				.text("Question " + id)
				.points(points)
				.correctOptionId(CORRECT_OPTION)
				.options(List.of(new AnswerOption(CORRECT_OPTION, "Correct answer"),
						new AnswerOption(WRONG_OPTION, "Wrong answer")))
				.build();
	}

	public static SubmitAnswersRequest answers(List<QuestionEntity> questions, int correctCount) {
		List<SubmittedAnswerDto> answers = new ArrayList<>();
		for (int index = 0; index < questions.size(); index++) {
			answers.add(new SubmittedAnswerDto(questions.get(index).getId(),
					index < correctCount ? CORRECT_OPTION : WRONG_OPTION));
		}
		return new SubmitAnswersRequest(List.copyOf(answers));
	}
}
