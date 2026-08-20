package com.example.quiz.controller;

import com.example.quiz.dto.OptionDto;
import com.example.quiz.dto.QuestionDto;
import com.example.quiz.dto.QuizRulesDto;
import com.example.quiz.dto.ScoringRuleDto;
import com.example.quiz.dto.ValidationErrorDto;
import com.example.quiz.exception.InvalidSubmissionException;
import com.example.quiz.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
class QuizControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private QuizService quizService;

	@Test
	void servesTheRulesNeededByTheIntroScreen() throws Exception {
		given(quizService.getRules()).willReturn(new QuizRulesDto("World Geography", "Some description",
				20, 1000, 600, List.of(new ScoringRuleDto(100, 3, 300))));

		mockMvc.perform(get("/api/v1/quiz"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.title").value("World Geography"))
				.andExpect(jsonPath("$.questionCount").value(20))
				.andExpect(jsonPath("$.maxScore").value(1000))
				.andExpect(jsonPath("$.passingScore").value(600))
				.andExpect(jsonPath("$.scoring[0].pointsPerQuestion").value(100));
	}

	@Test
	void servesQuestionsWithoutRevealingTheCorrectOption() throws Exception {
		given(quizService.getQuestions()).willReturn(List.of(new QuestionDto("q01",
				"What is the capital of France?", 25,
				List.of(new OptionDto("a", "Lyon"), new OptionDto("b", "Paris")))));

		String body = mockMvc.perform(get("/api/v1/quiz/questions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value("q01"))
				.andExpect(jsonPath("$[0].points").value(25))
				.andExpect(jsonPath("$[0].options.length()").value(2))
				.andReturn().getResponse().getContentAsString();

		assertThat(body).doesNotContain("correct");
	}

	@Test
	void answersAnInvalidSubmissionWithAProblemDetail() throws Exception {
		given(quizService.submit(any())).willThrow(new InvalidSubmissionException(
				List.of(ValidationErrorDto.optionNotInQuestion("q01", "z"))));

		mockMvc.perform(post("/api/v1/quiz/submissions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"answers\":[{\"questionId\":\"q01\",\"optionId\":\"z\"}]}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.title").value("Invalid submission"))
				.andExpect(jsonPath("$.errors[0].code").value("OPTION_NOT_IN_QUESTION"))
				.andExpect(jsonPath("$.errors[0].questionId").value("q01"));
	}

	@Test
	void rejectsASubmissionWithAnEmptyAnswerList() throws Exception {
		mockMvc.perform(post("/api/v1/quiz/submissions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"answers\":[]}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Malformed request"))
				.andExpect(jsonPath("$.errors[0].code").value("MALFORMED_REQUEST"));
	}

	@Test
	void rejectsAnAnswerWithABlankQuestionId() throws Exception {
		mockMvc.perform(post("/api/v1/quiz/submissions")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"answers\":[{\"questionId\":\"\",\"optionId\":\"a\"}]}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").value("Malformed request"));
	}
}
