package com.hospital.common.exception;

import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class Errors {
  @ExceptionHandler({
    DataIntegrityViolationException.class,
    org.springframework.dao.OptimisticLockingFailureException.class
  })
  ResponseEntity<ProblemDetail> conflict() {
    return problem(
        409, "Constraint conflict: duplicate booking, duplicate reference, or invalid state");
  }

  @ExceptionHandler(ResponseStatusException.class)
  ResponseEntity<ProblemDetail> status(ResponseStatusException e) {
    return problem(e.getStatusCode().value(), e.getReason());
  }

  @ExceptionHandler({
    IllegalArgumentException.class,
    MethodArgumentNotValidException.class,
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<ProblemDetail> invalid(Exception e) {
    return problem(400, "Invalid request data");
  }

  private ResponseEntity<ProblemDetail> problem(int status, String detail) {
    return ResponseEntity.status(status)
        .body(
            ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(status), detail == null ? "Request failed" : detail));
  }
}
