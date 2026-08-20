package com.example.quiz.dto;

public record ValidationErrorDto(ValidationErrorCode code, String questionId, String message) {

	public static ValidationErrorDto unknownQuestion(String questionId) {
		return new ValidationErrorDto(ValidationErrorCode.UNKNOWN_QUESTION, questionId,
				"Question '%s' does not belong to this quiz".formatted(questionId));
	}

	public static ValidationErrorDto duplicateAnswer(String questionId) {
		return new ValidationErrorDto(ValidationErrorCode.DUPLICATE_ANSWER, questionId,
				"Question '%s' is answered more than once".formatted(questionId));
	}

	public static ValidationErrorDto missingAnswer(String questionId) {
		return new ValidationErrorDto(ValidationErrorCode.MISSING_ANSWER, questionId,
				"Question '%s' has not been answered".formatted(questionId));
	}

	public static ValidationErrorDto optionNotInQuestion(String questionId, String optionId) {
		return new ValidationErrorDto(ValidationErrorCode.OPTION_NOT_IN_QUESTION, questionId,
				"Option '%s' is not one of the available options for question '%s'"
						.formatted(optionId, questionId));
	}

	/** Structural failures carry the rejected field path in place of a question id. */
	public static ValidationErrorDto malformedRequest(String fieldPath, String message) {
		return new ValidationErrorDto(ValidationErrorCode.MALFORMED_REQUEST, fieldPath, message);
	}
}
