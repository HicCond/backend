package com.example.quiz;

import com.example.quiz.entity.AnswerOption;
import com.example.quiz.entity.QuestionEntity;
import com.example.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeedDataTest {

	private static final Map<Integer, Long> REQUIRED_DISTRIBUTION = Map.of(
			100, 3L,
			75, 4L,
			40, 5L,
			25, 8L);

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void containsExactlyTwentyQuestionsWithUniqueIds() {
		List<QuestionEntity> questions = questionRepository.findAllByOrderByIdAsc();

		assertThat(questions).hasSize(20);
		assertThat(questions).extracting(QuestionEntity::getId).doesNotHaveDuplicates();
	}

	@Test
	void followsTheRequiredPointDistributionAndAddsUpToOneThousand() {
		List<QuestionEntity> questions = questionRepository.findAllByOrderByIdAsc();

		Map<Integer, Long> distribution = questions.stream()
				.collect(Collectors.groupingBy(QuestionEntity::getPoints, Collectors.counting()));

		assertThat(distribution).containsExactlyInAnyOrderEntriesOf(REQUIRED_DISTRIBUTION);
		assertThat(questions.stream().mapToInt(QuestionEntity::getPoints).sum()).isEqualTo(1000);
	}

	@Test
	void givesEveryQuestionThreeOrFourDistinctOptions() {
		for (QuestionEntity question : questionRepository.findAllByOrderByIdAsc()) {
			assertThat(question.getOptions())
					.as("options of %s", question.getId())
					.hasSizeBetween(3, 4)
					.extracting(AnswerOption::id)
					.doesNotHaveDuplicates();
		}
	}

	@Test
	void pointsEveryQuestionAtOneOfItsOwnOptionsAsTheCorrectAnswer() {
		for (QuestionEntity question : questionRepository.findAllByOrderByIdAsc()) {
			assertThat(question.getOptions())
					.as("correct option of %s", question.getId())
					.extracting(AnswerOption::id)
					.containsOnlyOnce(question.getCorrectOptionId());
		}
	}

	@Test
	void givesEveryQuestionAndOptionReadableText() {
		for (QuestionEntity question : questionRepository.findAllByOrderByIdAsc()) {
			assertThat(question.getText()).as("text of %s", question.getId()).isNotBlank();
			assertThat(question.getOptions())
					.as("option texts of %s", question.getId())
					.extracting(AnswerOption::text)
					.allSatisfy(text -> assertThat(text).isNotBlank());
		}
	}
}
