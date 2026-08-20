package com.example.quiz.exception;

import com.example.quiz.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidSubmissionException.class)
	public ProblemDetail handleInvalidSubmission(InvalidSubmissionException exception) {
		log.warn("Rejected submission: {}", exception.getMessage());
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
		problem.setTitle("Invalid submission");
		problem.setType(URI.create("urn:quiz:error:invalid-submission"));
		problem.setProperty("errors", exception.getErrors());
		return problem;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMalformedRequest(MethodArgumentNotValidException exception) {
		List<ValidationErrorDto> errors = exception.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> ValidationErrorDto.malformedRequest(fieldError.getField(),
						fieldError.getDefaultMessage()))
				.toList();
		log.warn("Rejected malformed request: {}", errors);
		ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"The request body does not match the expected format");
		problem.setTitle("Malformed request");
		problem.setType(URI.create("urn:quiz:error:malformed-request"));
		problem.setProperty("errors", errors);
		return problem;
	}
}
