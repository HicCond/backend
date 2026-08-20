package com.example.quiz.exception;

import com.example.quiz.dto.ValidationErrorDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvalidSubmissionException extends RuntimeException {

	transient List<ValidationErrorDto> errors;

	public InvalidSubmissionException(List<ValidationErrorDto> errors) {
		super("Submission contains %d invalid answer(s)".formatted(errors.size()));
		this.errors = List.copyOf(errors);
	}
}
