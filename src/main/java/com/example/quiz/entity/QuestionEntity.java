package com.example.quiz.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "question")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionEntity {

	@Id
	@Column(name = "id", length = 10)
	String id;

	@Column(name = "question_text", nullable = false, length = 500)
	String text;

	@Column(name = "points", nullable = false)
	int points;

	@Column(name = "correct_option_id", nullable = false, length = 5)
	String correctOptionId;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "question_option", joinColumns = @JoinColumn(name = "question_id"))
	@OrderColumn(name = "option_order")
	@AttributeOverrides({
			@AttributeOverride(name = "id", column = @Column(name = "option_id", nullable = false, length = 5)),
			@AttributeOverride(name = "text", column = @Column(name = "option_text", nullable = false, length = 300))
	})
	@Builder.Default
	List<AnswerOption> options = new ArrayList<>();

	public List<AnswerOption> getOptions() {
		return List.copyOf(options);
	}

	public boolean offersOption(String optionId) {
		return options.stream().anyMatch(option -> option.id().equals(optionId));
	}

	public boolean isCorrectOption(String optionId) {
		return correctOptionId.equals(optionId);
	}

	public String optionText(String optionId) {
		return options.stream()
				.filter(option -> option.id().equals(optionId))
				.map(AnswerOption::text)
				.findFirst()
				.orElse(null);
	}
}
