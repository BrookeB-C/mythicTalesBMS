package com.mythictales.bms.taplist.config;

import java.time.Instant;
import java.util.*;

import org.slf4j.MDC;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice(basePackages = "com.mythictales.bms.taplist.api")
public class RestExceptionHandler {

  public static record Problem(
      int status,
      String error,
      String message,
      Object details,
      Instant timestamp,
      String traceId) {}

  private ResponseEntity<Problem> build(HttpStatus status, String message, Object details) {
    String traceId = Optional.ofNullable(MDC.get("traceId")).orElse(MDC.get("X-Request-Id"));
    Problem body = new Problem(status.value(), status.getReasonPhrase(), message, details, Instant.now(), traceId);
    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Problem> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (var err : ex.getBindingResult().getAllErrors()) {
      String field = err instanceof FieldError fe ? fe.getField() : err.getObjectName();
      errors.put(field, err.getDefaultMessage());
    }
    return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Problem> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
      errors.put(String.valueOf(v.getPropertyPath()), v.getMessage());
    }
    return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Problem> handleAccessDenied(AccessDeniedException ex) {
    return build(HttpStatus.FORBIDDEN, "Forbidden", null);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Problem> handleNotFound(NoSuchElementException ex) {
    return build(HttpStatus.NOT_FOUND, "Not found", null);
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ResponseEntity<Problem> handleOptimisticLock(OptimisticLockingFailureException ex) {
    return build(HttpStatus.CONFLICT, "Optimistic lock conflict", null);
  }

  @ExceptionHandler(com.mythictales.bms.taplist.service.BusinessValidationException.class)
  public ResponseEntity<Problem> handleBusinessValidation(
      com.mythictales.bms.taplist.service.BusinessValidationException ex) {
    return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), ex.getDetails());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Problem> handleGeneric(Exception ex, WebRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", null);
  }
}
