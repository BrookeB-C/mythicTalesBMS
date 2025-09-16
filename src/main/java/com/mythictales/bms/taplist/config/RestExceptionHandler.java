package com.mythictales.bms.taplist.config;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mythictales.bms.taplist.catalog.service.RecipeImportService.DuplicateRecipeException;
import com.mythictales.bms.taplist.service.BusinessValidationException;

@ControllerAdvice
public class RestExceptionHandler {
  private Map<String, Object> body(int status, String error, String message, Object details) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("status", status);
    m.put("error", error);
    if (message != null) m.put("message", message);
    if (details != null) m.put("details", details);
    m.put("timestamp", Instant.now().toString());
    return m;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handle403(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body(403, "Forbidden", ex.getMessage(), null));
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<?> handle400(Exception ex) {
    Object details = null;
    if (ex instanceof MethodArgumentNotValidException manv) {
      var br = manv.getBindingResult();
      Map<String, String> errs = new LinkedHashMap<>();
      br.getFieldErrors().forEach(fe -> errs.put(fe.getField(), fe.getDefaultMessage()));
      details = errs;
    } else if (ex instanceof BindException be) {
      var br = be.getBindingResult();
      Map<String, String> errs = new LinkedHashMap<>();
      br.getFieldErrors().forEach(fe -> errs.put(fe.getField(), fe.getDefaultMessage()));
      details = errs;
    }
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body(400, "Bad Request", "Validation failed", details));
  }

  @ExceptionHandler(BusinessValidationException.class)
  public ResponseEntity<?> handle422(BusinessValidationException ex) {
    return ResponseEntity.status(422)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body(422, "Unprocessable Entity", ex.getMessage(), ex.getDetails()));
  }

  @ExceptionHandler({OptimisticLockingFailureException.class, DuplicateRecipeException.class})
  public ResponseEntity<?> handle409(Exception ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body(409, "Conflict", ex.getMessage(), null));
  }

  @ExceptionHandler({
    java.util.NoSuchElementException.class,
    jakarta.persistence.EntityNotFoundException.class
  })
  public ResponseEntity<?> handle404(Exception ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            body(404, "Not Found", ex.getMessage() != null ? ex.getMessage() : "Not found", null));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handle500(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body(500, "Internal Server Error", ex.getMessage(), null));
  }
}
