package com.example.quiz;

import com.example.quiz.entity.AnswerOption;
import com.example.quiz.entity.QuestionEntity;
import com.example.quiz.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuizApiIntegrationTest {

	private static final String SUBMISSIONS_URL = "/api/v1/quiz/submissions";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void servesTheQuizRulesForTheIntroScreen() throws Exception {
		mockMvc.perform(get("/api/v1/quiz"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("World Geography"))
				.andExpect(jsonPath("$.questionCount").value(20))
				.andExpect(jsonPath("$.maxScore").value(1000))
				.andExpect(jsonPath("$.passingScore").value(600))
				.andExpect(jsonPath("$.scoring.length()").value(4))
				.andExpect(jsonPath("$.scoring[0].pointsPerQuestion").value(100))
				.andExpect(jsonPath("$.scoring[0].questionCount").value(3))
				.andExpect(jsonPath("$.scoring[0].subtotal").value(300));
	}

	@Test
	void servesAllTwentyQuestionsWithoutTheCorrectAnswers() throws Exception {
		String body = mockMvc.perform(get("/api/v1/quiz/questions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(20))
				.andExpect(jsonPath("$[0].id").value("q01"))
				.andExpect(jsonPath("$[0].points").value(100))
				.andReturn().getResponse().getContentAsString();

		assertThat(body).doesNotContain("correct");
	}

	@Test
	void scoresAFullyCorrectSubmissionAsPassed() throws Exception {
		mockMvc.perform(post(SUBMISSIONS_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(submissionBody(true)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalScore").value(1000))
				.andExpect(jsonPath("$.maxScore").value(1000))
				.andExpect(jsonPath("$.correctCount").value(20))
				.andExpect(jsonPath("$.incorrectCount").value(0))
				.andExpect(jsonPath("$.passed").value(true))
				.andExpect(jsonPath("$.incorrectAnswers.length()").value(0));
	}

	@Test
	void scoresAFullyWrongSubmissionAsFailedAndRevealsTheCorrectAnswers() throws Exception {
		mockMvc.perform(post(SUBMISSIONS_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(submissionBody(false)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalScore").value(0))
				.andExpect(jsonPath("$.correctCount").value(0))
				.andExpect(jsonPath("$.incorrectCount").value(20))
				.andExpect(jsonPath("$.passed").value(false))
				.andExpect(jsonPath("$.incorrectAnswers.length()").value(20))
				.andExpect(jsonPath("$.incorrectAnswers[0].questionText").isNotEmpty())
				.andExpect(jsonPath("$.incorrectAnswers[0].correctOptionId").value("a"))
				.andExpect(jsonPath("$.incorrectAnswers[0].correctOptionText").value("Mount Kosciuszko"));
	}

	@Test
	void rejectsAnOptionThatDoesNotBelongToTheAnsweredQuestion() throws Exception {
		String body = submissionBody(true).replace("{\"questionId\":\"q12\",\"optionId\":\"a\"}",
				"{\"questionId\":\"q12\",\"optionId\":\"d\"}");

		mockMvc.perform(post(SUBMISSIONS_URL).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Invalid submission"))
				.andExpect(jsonPath("$.errors.length()").value(1))
				.andExpect(jsonPath("$.errors[0].code").value("OPTION_NOT_IN_QUESTION"))
				.andExpect(jsonPath("$.errors[0].questionId").value("q12"));
	}

	@Test
	void rejectsAnIncompleteSubmission() throws Exception {
		mockMvc.perform(post(SUBMISSIONS_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"answers\":[{\"questionId\":\"q01\",\"optionId\":\"a\"}]}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Invalid submission"))
				.andExpect(jsonPath("$.errors.length()").value(19))
				.andExpect(jsonPath("$.errors[0].code").value("MISSING_ANSWER"));
	}

	@Test
	void rejectsAnAnswerForAQuestionThatIsNotPartOfTheQuiz() throws Exception {
		String body = submissionBody(true).replace("\"answers\":[",
				"\"answers\":[{\"questionId\":\"q99\",\"optionId\":\"a\"},");

		mockMvc.perform(post(SUBMISSIONS_URL).contentType(MediaType.APPLICATION_JSON).content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].code").value("UNKNOWN_QUESTION"))
				.andExpect(jsonPath("$.errors[0].questionId").value("q99"));
	}

	private String submissionBody(boolean answerCorrectly) {
		String answers = questionRepository.findAllByOrderByIdAsc().stream()
				.map(question -> "{\"questionId\":\"%s\",\"optionId\":\"%s\"}".formatted(
						question.getId(),
						answerCorrectly ? question.getCorrectOptionId() : anyWrongOption(question)))
				.collect(Collectors.joining(","));
		return "{\"answers\":[" + answers + "]}";
	}

	private String anyWrongOption(QuestionEntity question) {
		List<String> optionIds = question.getOptions().stream().map(AnswerOption::id).toList();
		return optionIds.stream()
				.filter(optionId -> !optionId.equals(question.getCorrectOptionId()))
				.findFirst()
				.orElseThrow();
	}
}
